package com.example.cube.modules.knowledge.repository;

import com.example.cube.modules.knowledge.entity.KnowledgeKeyword;
import com.example.cube.modules.knowledge.entity.KnowledgeKeywordId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KnowledgeKeywordRepository extends JpaRepository<KnowledgeKeyword, KnowledgeKeywordId> {
    List<KnowledgeKeyword> findByKnowledgeId(Long knowledgeId);
}
