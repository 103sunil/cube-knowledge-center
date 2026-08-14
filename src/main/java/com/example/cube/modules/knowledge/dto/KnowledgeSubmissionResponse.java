package com.example.cube.modules.knowledge.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KnowledgeSubmissionResponse {
    private KnowledgeResponse knowledge;
    private List<AttachmentResponse> attachments;
}