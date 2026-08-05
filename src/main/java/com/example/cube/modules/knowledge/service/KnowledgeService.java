package com.example.cube.modules.knowledge.service;

import com.example.cube.modules.knowledge.dto.KnowledgeCreateRequest;
import com.example.cube.modules.knowledge.dto.KnowledgeResponse;
import com.example.cube.modules.knowledge.entity.*;
import com.example.cube.modules.knowledge.repository.*;
import com.example.cube.modules.auth.entity.UserMaster;
import com.example.cube.modules.auth.repository.UserMasterRepository;
import com.example.cube.modules.auth.service.AccessControlService;
import com.example.cube.common.exception.AccessDeniedAppException;
import com.example.cube.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private static final String MODULE = "KNOWLEDGE";

    private final KnowledgeRepository knowledgeRepository;
    private final KeywordRepository keywordRepository;
    private final KnowledgeKeywordRepository knowledgeKeywordRepository;
    private final UserMasterRepository userMasterRepository;
    private final AccessControlService accessControlService;

    @Transactional
    public KnowledgeResponse create(KnowledgeCreateRequest request, Authentication auth) {
        requirePermission(auth, "CREATE");

        UserMaster user = currentUser(auth);

        Knowledge knowledge = Knowledge.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status("PENDING")
                .createdBy(user.getUserId())
                .build();
        knowledge = knowledgeRepository.save(knowledge);

        for (String rawKeyword : request.getKeywords()) {
            String name = rawKeyword.trim();
            if (name.isEmpty()) continue;
            Keyword keyword = keywordRepository.findByKeywordNameIgnoreCase(name)
                    .orElseGet(() -> keywordRepository.save(Keyword.builder().keywordName(name).build()));
            knowledgeKeywordRepository.save(KnowledgeKeyword.builder()
                    .knowledgeId(knowledge.getKnowledgeId())
                    .keywordId(keyword.getKeywordId())
                    .build());
        }

        return toResponse(knowledge);
    }

    public Page<KnowledgeResponse> listPending(Authentication auth, Pageable pageable) {
        requirePermission(auth, "APPROVE");
        return knowledgeRepository.findByStatus("PENDING", pageable).map(this::toResponse);
    }

    @Transactional
    public KnowledgeResponse approve(Long knowledgeId, Authentication auth) {
        requirePermission(auth, "APPROVE");
        Knowledge knowledge = findOrThrow(knowledgeId);
        UserMaster reviewer = currentUser(auth);

        knowledge.setStatus("PUBLISHED");
        knowledge.setReviewedBy(reviewer.getUserId());
        knowledge.setReviewedAt(LocalDateTime.now());
        return toResponse(knowledgeRepository.save(knowledge));
    }

    @Transactional
    public KnowledgeResponse reject(Long knowledgeId, String reason, Authentication auth) {
        requirePermission(auth, "APPROVE");
        Knowledge knowledge = findOrThrow(knowledgeId);
        UserMaster reviewer = currentUser(auth);

        knowledge.setStatus("REJECTED");
        knowledge.setReviewedBy(reviewer.getUserId());
        knowledge.setReviewedAt(LocalDateTime.now());
        knowledge.setRejectionReason(reason);
        return toResponse(knowledgeRepository.save(knowledge));
    }

    public KnowledgeResponse getById(Long knowledgeId) {
        return toResponse(findOrThrow(knowledgeId));
    }

    private Knowledge findOrThrow(Long knowledgeId) {
        return knowledgeRepository.findById(knowledgeId)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge not found: " + knowledgeId));
    }

    private UserMaster currentUser(Authentication auth) {
        return userMasterRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void requirePermission(Authentication auth, String accessCode) {
        if (!accessControlService.hasPermission(auth.getName(), MODULE, accessCode)) {
            throw new AccessDeniedAppException("Not authorized to " + accessCode + " knowledge");
        }
    }

    private KnowledgeResponse toResponse(Knowledge k) {
        List<String> keywords = knowledgeKeywordRepository.findByKnowledgeId(k.getKnowledgeId()).stream()
                .map(kk -> keywordRepository.findById(kk.getKeywordId()).map(Keyword::getKeywordName).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        return KnowledgeResponse.builder()
                .knowledgeId(k.getKnowledgeId())
                .title(k.getTitle())
                .description(k.getDescription())
                .status(k.getStatus())
                .createdBy(k.getCreatedBy())
                .createdAt(k.getCreatedAt())
                .reviewedBy(k.getReviewedBy())
                .reviewedAt(k.getReviewedAt())
                .rejectionReason(k.getRejectionReason())
                .keywords(keywords)
                .build();
    }
}
