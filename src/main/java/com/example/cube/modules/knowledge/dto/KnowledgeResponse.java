package com.example.cube.modules.knowledge.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KnowledgeResponse {
    private Long knowledgeId;
    private String title;
    private String description;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectionReason;
    private List<String> keywords;
}
