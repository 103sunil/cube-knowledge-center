package com.example.cube.modules.knowledge.repository;

import com.example.cube.modules.knowledge.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByKeywordNameIgnoreCase(String keywordName);
}
