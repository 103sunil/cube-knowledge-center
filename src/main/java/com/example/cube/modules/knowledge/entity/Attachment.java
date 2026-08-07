package com.example.cube.modules.knowledge.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CU_ATTACHMENT")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTACHMENT_ID")
    private Long attachmentId;

    @Column(name = "KNOWLEDGE_ID", nullable = false)
    private Long knowledgeId;

    @Column(name = "FILE_NAME", length = 255)
    private String fileName;

    @Column(name = "CONTENT_TYPE", length = 100)
    private String contentType;

    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Column(name = "ONEDRIVE_FILE_ID", length = 255)
    private String onedriveFileId;

    @Column(name = "UPLOADED_AT")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
