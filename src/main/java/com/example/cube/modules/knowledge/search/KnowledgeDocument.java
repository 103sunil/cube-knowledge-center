package com.example.cube.modules.knowledge.search;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * Only PUBLISHED knowledge ever gets written here (see KnowledgeService.approve()).
 * This index is intentionally never populated with PENDING/REJECTED/DRAFT items -
 * that's what keeps the search endpoint safe to expose to any authenticated
 * employee without a separate status filter.
 */
@Document(indexName = "knowledge")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KnowledgeDocument {

    @Id
    private String id; // Knowledge.knowledgeId as a string

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private List<String> keywords;

    @Field(type = FieldType.Long)
    private Long createdBy;
}