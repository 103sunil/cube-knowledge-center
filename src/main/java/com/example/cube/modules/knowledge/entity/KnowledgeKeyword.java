package com.example.cube.modules.knowledge.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "KNOWLEDGE_KEYWORD")
@IdClass(KnowledgeKeywordId.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KnowledgeKeyword {

    @Id
    @Column(name = "KNOWLEDGE_ID")
    private Long knowledgeId;

    @Id
    @Column(name = "KEYWORD_ID")
    private Long keywordId;
}
