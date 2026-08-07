package com.example.cube.modules.knowledge.controller;

import com.example.cube.modules.knowledge.dto.AttachmentResponse;
import com.example.cube.modules.knowledge.service.AttachmentService;
import com.example.cube.modules.knowledge.service.DownloadedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping("/knowledge/{knowledgeId}/attachments")
    public ResponseEntity<AttachmentResponse> upload(@PathVariable Long knowledgeId,
                                                       @RequestParam("file") MultipartFile file,
                                                       Authentication auth) {
        return ResponseEntity.ok(attachmentService.upload(knowledgeId, file, auth));
    }

    @GetMapping("/knowledge/{knowledgeId}/attachments")
    public ResponseEntity<List<AttachmentResponse>> list(@PathVariable Long knowledgeId, Authentication auth) {
        return ResponseEntity.ok(attachmentService.listByKnowledge(knowledgeId, auth));
    }

    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id, Authentication auth) {
        DownloadedFile file = attachmentService.download(id, auth);
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(file.getContent());
    }
}