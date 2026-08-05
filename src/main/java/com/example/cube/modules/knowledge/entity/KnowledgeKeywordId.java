package com.example.cube.modules.knowledge.entity;

import lombok.*;
import java.io.Serializable;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class KnowledgeKeywordId implements Serializable {
    private Long knowledgeId;
    private Long keywordId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KnowledgeKeywordId)) return false;
        KnowledgeKeywordId that = (KnowledgeKeywordId) o;
        return Objects.equals(knowledgeId, that.knowledgeId) && Objects.equals(keywordId, that.keywordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(knowledgeId, keywordId);
    }
}
