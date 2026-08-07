package com.example.cube.common.storage.onedrive;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

/**
 * App-only (client credentials) auth against Microsoft identity platform.
 * Requires an Azure AD app registration with Sites.ReadWrite.All (or
 * Files.ReadWrite.All, depending on whether this targets a SharePoint
 * document library or a plain OneDrive) granted as an application permission
 * with admin consent - a delegated/user token will NOT work here since
 * there's no interactive user in this flow.
 */
@Service
public class GraphTokenService {

    @Value("${graph.tenant-id}")
    private String tenantId;

    @Value("${graph.client-id}")
    private String clientId;

    @Value("${graph.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    private volatile String cachedToken;
    private volatile Instant expiresAt = Instant.EPOCH;

    public GraphTokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public synchronized String getAccessToken() {
        if (cachedToken != null && Instant.now().isBefore(expiresAt.minusSeconds(60))) {
            return cachedToken;
        }

        String url = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("scope", "https://graph.microsoft.com/.default");
        form.add("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> response =
                (ResponseEntity<Map<String, Object>>) (ResponseEntity<?>) restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null || body.get("access_token") == null) {
            throw new IllegalStateException("Failed to acquire Graph API token");
        }

        cachedToken = (String) body.get("access_token");
        int expiresIn = ((Number) body.get("expires_in")).intValue();
        expiresAt = Instant.now().plusSeconds(expiresIn);

        return cachedToken;
    }
}