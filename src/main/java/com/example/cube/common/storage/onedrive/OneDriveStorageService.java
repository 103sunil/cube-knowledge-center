package com.example.cube.common.storage.onedrive;

import com.example.cube.common.storage.FileStorageService;
import com.example.cube.common.storage.StoredFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Profile;

import java.util.Map;

/**
 * Uses the Graph API "simple upload" (PUT .../content), which caps out at
 * 4MB per file - see AttachmentService.MAX_FILE_SIZE. Anything larger needs
 * a resumable upload session, not implemented here yet.
 */
@Service
@Profile("!local")
public class OneDriveStorageService implements FileStorageService {

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
        String path = baseFolder + "/" + folderPath + "/" + fileName;
        String url = "https://graph.microsoft.com/v1.0/drives/" + driveId + "/root:" + path + ":/content";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getAccessToken());
        headers.setContentType(MediaType.parseMediaType(
                contentType != null ? contentType : "application/octet-stream"));

        HttpEntity<byte[]> request = new HttpEntity<>(content, headers);

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> response =
                (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>)
                        restTemplate.exchange(url, HttpMethod.PUT, request, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || body.get("id") == null) {
            throw new IllegalStateException("OneDrive upload did not return an item id");
        }

        String itemId = (String) body.get("id");
        return new StoredFile(itemId, content.length);
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
}