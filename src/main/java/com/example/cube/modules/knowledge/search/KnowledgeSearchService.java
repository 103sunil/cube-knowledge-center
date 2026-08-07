package com.example.cube.modules.knowledge.search;

import com.example.cube.modules.knowledge.entity.Knowledge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeSearchService {

    private final KnowledgeSearchRepository searchRepository;

    public void index(Knowledge knowledge, List<String> keywords) {
        KnowledgeDocument doc = KnowledgeDocument.builder()
                .id(String.valueOf(knowledge.getKnowledgeId()))
                .title(knowledge.getTitle())
                .description(knowledge.getDescription())
                .keywords(keywords)
                .createdBy(knowledge.getCreatedBy())
                .build();
        searchRepository.save(doc);
    }

    /** Returns matching knowledge_ids, deduplicated, in the order Elasticsearch ranked them. */
    public List<Long> search(String query) {
        Set<Long> ids = new LinkedHashSet<>();
        searchRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrKeywordsContainingIgnoreCase(
                        query, query, query)
                .forEach(doc -> ids.add(Long.valueOf(doc.getId())));
        return ids.stream().collect(Collectors.toList());
    }
}