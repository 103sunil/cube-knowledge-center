package com.example.cube.common.storage.onedrive;

import com.example.cube.common.exception.FileStorageException;
import com.example.cube.common.storage.FileStorageService;
import com.example.cube.common.storage.StoredFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

/**
 * Files up to 4MB use the Graph API "simple upload" (single PUT .../content).
 * Above 4MB - up to AttachmentService.MAX_FILE_SIZE (10MB) - this switches to
 * a resumable upload session instead, since Graph's simple upload hard-caps
 * at 4MB regardless of what this app's own limit is. A 10MB file still fits
 * in a single PUT against the session's uploadUrl (Graph supports large
 * single-shot fragments well beyond 10MB) - true multi-chunk uploading isn't
 * needed at this size and isn't implemented.
 *
 * Active unless the "local" Spring profile is set - see LocalDiskStorageService
 * for the swap-in used during local/Postman testing without real Azure creds.
 */
@Service
@Profile("!local")
public class OneDriveStorageService implements FileStorageService {

    private static final long SIMPLE_UPLOAD_LIMIT = 4L * 1024 * 1024;

    @Value("${graph.drive-id}")
    private String driveId;

    @Value("${graph.base-folder:/CubeKnowledgeCenter}")
    private String baseFolder;

    private final RestTemplate restTemplate;
    private final GraphTokenService tokenService;

    public OneDriveStorageService(RestTemplate restTemplate, GraphTokenService tokenService) {
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
    }

    @Override
    public StoredFile upload(String folderPath, String fileName, String contentType, byte[] content) {
        if (content.length <= SIMPLE_UPLOAD_LIMIT) {
            return simpleUpload(folderPath, fileName, contentType, content);
        }
        return resumableUpload(folderPath, fileName, contentType, content);
    }

    private StoredFile simpleUpload(String folderPath, String fileName, String contentType, byte[] content) {
        String path = baseFolder + "/" + folderPath + "/" + fileName;
        String url = "https://graph.microsoft.com/v1.0/drives/" + driveId + "/root:" + path + ":/content";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getAccessToken());
        headers.setContentType(MediaType.parseMediaType(
                contentType != null ? contentType : "application/octet-stream"));

        HttpEntity<byte[]> request = new HttpEntity<>(content, headers);

        Map<String, Object> body = exchangeForMap(url, HttpMethod.PUT, request);
        return storedFileFrom(body, content.length);
    }

    private StoredFile resumableUpload(String folderPath, String fileName, String contentType, byte[] content) {
        String path = baseFolder + "/" + folderPath + "/" + fileName;
        String sessionUrl = "https://graph.microsoft.com/v1.0/drives/" + driveId
                + "/root:" + path + ":/createUploadSession";

        HttpHeaders sessionHeaders = new HttpHeaders();
        sessionHeaders.setBearerAuth(tokenService.getAccessToken());
        sessionHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> sessionRequest = new HttpEntity<>(Collections.emptyMap(), sessionHeaders);

        Map<String, Object> sessionBody = exchangeForMap(sessionUrl, HttpMethod.POST, sessionRequest);
        String uploadUrl = (String) sessionBody.get("uploadUrl");
        if (uploadUrl == null) {
            throw new FileStorageException("OneDrive did not return an upload session URL for: " + fileName);
        }

        HttpHeaders uploadHeaders = new HttpHeaders();
        uploadHeaders.setContentType(MediaType.parseMediaType(
                contentType != null ? contentType : "application/octet-stream"));
        uploadHeaders.set("Content-Range", "bytes 0-" + (content.length - 1) + "/" + content.length);
        HttpEntity<byte[]> uploadRequest = new HttpEntity<>(content, uploadHeaders);

        Map<String, Object> body = exchangeForMap(uploadUrl, HttpMethod.PUT, uploadRequest);
        return storedFileFrom(body, content.length);
    }

    @Override
    public byte[] download(String externalFileId) {
        String url = "https://graph.microsoft.com/v1.0/drives/" + driveId + "/items/" + externalFileId + "/content";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getAccessToken());
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, request, byte[].class);
        return response.getBody();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> exchangeForMap(String url, HttpMethod method, HttpEntity<?> request) {
        ResponseEntity<Map> response = restTemplate.exchange(url, method, request, Map.class);
        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new FileStorageException("Empty response from OneDrive for " + method + " " + url);
        }
        return body;
    }

    private StoredFile storedFileFrom(Map<String, Object> body, long size) {
        String itemId = (String) body.get("id");
        if (itemId == null) {
            throw new FileStorageException("OneDrive response did not include an item id");
        }
        return new StoredFile(itemId, size);
    }
}