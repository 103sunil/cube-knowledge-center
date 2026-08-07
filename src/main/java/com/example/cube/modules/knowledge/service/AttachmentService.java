package com.example.cube.modules.knowledge.service;

import com.example.cube.common.exception.AccessDeniedAppException;
import com.example.cube.common.exception.FileValidationException;
import com.example.cube.common.exception.ResourceNotFoundException;
import com.example.cube.common.storage.FileStorageService;
import com.example.cube.common.storage.StoredFile;
import com.example.cube.modules.auth.entity.UserMaster;
import com.example.cube.modules.auth.repository.UserMasterRepository;
import com.example.cube.modules.auth.service.AccessControlService;
import com.example.cube.modules.knowledge.dto.AttachmentResponse;
import com.example.cube.modules.knowledge.entity.Attachment;
import com.example.cube.modules.knowledge.entity.Knowledge;
import com.example.cube.modules.knowledge.repository.AttachmentRepository;
import com.example.cube.modules.knowledge.repository.KnowledgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final String MODULE = "KNOWLEDGE";
    private static final long MAX_FILE_SIZE = 4L * 1024 * 1024; // Graph simple-upload limit

    private final AttachmentRepository attachmentRepository;
    private final KnowledgeRepository knowledgeRepository;
    private final UserMasterRepository userMasterRepository;
    private final AccessControlService accessControlService;
    private final FileStorageService fileStorageService;
    private final KnowledgeService knowledgeService;

    @Transactional
    public AttachmentResponse upload(Long knowledgeId, MultipartFile file, Authentication auth) {
        requirePermission(auth, "CREATE");

        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge not found: " + knowledgeId));

        UserMaster user = currentUser(auth);
        if (!knowledge.getCreatedBy().equals(user.getUserId())) {
            throw new AccessDeniedAppException("Only the submitter can attach files to this knowledge item");
        }
        if (!"PENDING".equals(knowledge.getStatus())) {
            throw new AccessDeniedAppException("Cannot attach files once a knowledge item is no longer pending");
        }
        if (file.isEmpty()) {
            throw new FileValidationException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File exceeds the 4MB limit");
        }

        StoredFile stored;
        try {
            stored = fileStorageService.upload(
                    "knowledge-" + knowledgeId, file.getOriginalFilename(), file.getContentType(), file.getBytes());
        } catch (IOException e) {
            throw new FileValidationException("Failed to read uploaded file");
        }

        Attachment attachment = Attachment.builder()
                .knowledgeId(knowledgeId)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(stored.getSize())
                .onedriveFileId(stored.getExternalId())
                .build();

        return toResponse(attachmentRepository.save(attachment));
    }

    public List<AttachmentResponse> listByKnowledge(Long knowledgeId, Authentication auth) {
        requirePermission(auth, "VIEW");
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge not found: " + knowledgeId));
        knowledgeService.assertViewable(knowledge, auth);

        return attachmentRepository.findByKnowledgeId(knowledgeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DownloadedFile download(Long attachmentId, Authentication auth) {
        requirePermission(auth, "DOWNLOAD");

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found: " + attachmentId));
        Knowledge knowledge = knowledgeRepository.findById(attachment.getKnowledgeId())
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge not found: " + attachment.getKnowledgeId()));
        knowledgeService.assertViewable(knowledge, auth);

        byte[] content = fileStorageService.download(attachment.getOnedriveFileId());
        return new DownloadedFile(attachment.getFileName(), attachment.getContentType(), content);
    }

    private UserMaster currentUser(Authentication auth) {
        return userMasterRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void requirePermission(Authentication auth, String accessCode) {
        if (!accessControlService.hasPermission(auth.getName(), MODULE, accessCode)) {
            throw new AccessDeniedAppException("Not authorized to " + accessCode + " attachments");
        }
    }

    private AttachmentResponse toResponse(Attachment a) {
        return AttachmentResponse.builder()
                .attachmentId(a.getAttachmentId())
                .knowledgeId(a.getKnowledgeId())
                .fileName(a.getFileName())
                .contentType(a.getContentType())
                .fileSize(a.getFileSize())
                .uploadedAt(a.getUploadedAt())
                .build();
    }
}