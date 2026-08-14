package com.example.cube.modules.knowledge.service;

import com.example.cube.common.exception.FileValidationException;
import com.example.cube.modules.knowledge.dto.AttachmentResponse;
import com.example.cube.modules.knowledge.dto.KnowledgeCreateRequest;
import com.example.cube.modules.knowledge.dto.KnowledgeResponse;
import com.example.cube.modules.knowledge.dto.KnowledgeSubmissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KnowledgeSubmissionService {

    private final KnowledgeService knowledgeService;
    private final AttachmentService attachmentService;

    @Transactional
    public KnowledgeSubmissionResponse submit(String title,
                                               String description,
                                               List<String> keywords,
                                               List<MultipartFile> files,
                                               Authentication auth) {
        if (title == null || title.trim().isEmpty()) {
            throw new FileValidationException("Title is required");
        }
        if (keywords == null || keywords.isEmpty()) {
            throw new FileValidationException("At least one keyword is required");
        }
        if (files == null || files.isEmpty()) {
            throw new FileValidationException("At least one attachment is required");
        }

        KnowledgeCreateRequest request = new KnowledgeCreateRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setKeywords(keywords);

        KnowledgeResponse knowledge = knowledgeService.create(request, auth);
        List<AttachmentResponse> attachments =
                attachmentService.uploadBatch(knowledge.getKnowledgeId(), files, auth);

        return KnowledgeSubmissionResponse.builder()
                .knowledge(knowledge)
                .attachments(attachments)
                .build();
    }
}