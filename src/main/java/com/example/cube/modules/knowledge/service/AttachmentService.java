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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private static final String MODULE = "KNOWLEDGE";

    // Raised from 4MB to 10MB per requirement. Local disk storage has no
    // ceiling of its own, but OneDrive's Graph API "simple upload" caps at
    // 4MB - anything above that goes through OneDriveStorageService's
    // resumable-upload-session path instead. See that class for the split.
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    private final AttachmentRepository attachmentRepository;
    private final KnowledgeRepository knowledgeRepository;
    private final UserMasterRepository userMasterRepository;
    private final AccessControlService accessControlService;
    private final FileStorageService fileStorageService;
    private final KnowledgeService knowledgeService;

    @Transactional
    public AttachmentResponse upload(Long knowledgeId, MultipartFile file, Authentication auth) {
        requirePermission(auth, "CREATE");
        Knowledge knowledge = fetchAndAuthorizeUpload(knowledgeId, auth);
        validateFile(file);
        return storeAndSave(knowledge.getKnowledgeId(), file);
    }

    /**
     * Multiple files in a single request - the frontend stages files locally
     * and calls this once, with everything, on the actual Submit click.
     *
     * All files are validated up front, before any are written to storage.
     * This avoids the case where file 3 of 4 fails validation after 1 and 2
     * have already been written to disk/OneDrive - storage writes aren't part
     * of the DB transaction, so once bytes are written there's no automatic
     * rollback of them, only of the CU_ATTACHMENT row.
     */
    @Transactional
    public List<AttachmentResponse> uploadBatch(Long knowledgeId, List<MultipartFile> files, Authentication auth) {
        requirePermission(auth, "CREATE");
        Knowledge knowledge = fetchAndAuthorizeUpload(knowledgeId, auth);

        if (files == null || files.isEmpty()) {
            throw new FileValidationException("At least one file is required");
        }
        for (MultipartFile file : files) {
            validateFile(file);
        }

        List<AttachmentResponse> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(storeAndSave(knowledge.getKnowledgeId(), file));
        }
        return results;
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

    /**
     * Ownership + status gate shared by upload() and uploadBatch(). Only
     * REJECTED blocks new attachments - PENDING and PUBLISHED are both fine,
     * because a manager's own submission is PUBLISHED immediately at creation
     * (see KnowledgeService.create()) and still needs to accept the files
     * being attached as part of that same submit action.
     */
    private Knowledge fetchAndAuthorizeUpload(Long knowledgeId, Authentication auth) {
        Knowledge knowledge = knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge not found: " + knowledgeId));

        UserMaster user = currentUser(auth);
        if (!knowledge.getCreatedBy().equals(user.getUserId())) {
            throw new AccessDeniedAppException("Only the submitter can attach files to this knowledge item");
        }
        if ("REJECTED".equals(knowledge.getStatus())) {
            throw new AccessDeniedAppException("Cannot attach files to a rejected knowledge item");
        }
        return knowledge;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileValidationException("File is empty: " + file.getOriginalFilename());
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File exceeds the 10MB limit: " + file.getOriginalFilename());
        }
    }

    private AttachmentResponse storeAndSave(Long knowledgeId, MultipartFile file) {
        StoredFile stored;
        try {
            stored = fileStorageService.upload(
                    "knowledge-" + knowledgeId, file.getOriginalFilename(), file.getContentType(), file.getBytes());
        } catch (IOException e) {
            throw new FileValidationException("Failed to read uploaded file: " + file.getOriginalFilename());
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