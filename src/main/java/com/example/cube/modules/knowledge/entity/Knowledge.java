package com.example.cube.modules.knowledge.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CU_KNOWLEDGE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Knowledge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KNOWLEDGE_ID")
    private Long knowledgeId;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STATUS", length = 20)
    private String status;

    @Column(name = "CREATED_BY", nullable = false)
    private Long createdBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "REVIEWED_BY")
    private Long reviewedBy;

    @Column(name = "REVIEWED_AT")
    private LocalDateTime reviewedAt;

    @Lob
    @Column(name = "REJECTION_REASON")
    private String rejectionReason;

    // Denormalized title + description + keywords, kept in sync by KnowledgeService
    // on every create/keyword change. Oracle Text indexes this column (see
    // sql/schema.sql) - this is what CONTAINS() in KnowledgeRepository.searchPublished
    // actually searches. Never set directly outside KnowledgeService.
    @Lob
    @Column(name = "SEARCH_TEXT")
    private String searchText;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "DRAFT";
        createdAt = LocalDateTime.now();
    }
}