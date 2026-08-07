package com.example.cube.modules.knowledge.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface KnowledgeSearchRepository extends ElasticsearchRepository<KnowledgeDocument, String> {

    List<KnowledgeDocument> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrKeywordsContainingIgnoreCase(
            String title, String description, String keyword);
}