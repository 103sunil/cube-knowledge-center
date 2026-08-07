package com.example.cube.modules.knowledge.repository;

import com.example.cube.modules.knowledge.entity.Knowledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {
    Page<Knowledge> findByStatus(String status, Pageable pageable);
    List<Knowledge> findByKnowledgeIdIn(List<Long> ids);

    /**
     * Oracle Text full-text search over SEARCH_TEXT (title + description + keywords,
     * kept in sync by KnowledgeService). CONTAINS()/SCORE() are Oracle Text SQL
     * functions - the query string is passed straight through to the CTXSYS engine,
     * so this can't be expressed as a Spring Data derived method name.
     *
     * status = 'PUBLISHED' is enforced here, not just at the caller - this is the
     * actual guarantee that PENDING/REJECTED/DRAFT items can never appear in search
     * results, even though their SEARCH_TEXT column is populated like everyone else's.
     */
    @Query(value = "SELECT k.* FROM CU_KNOWLEDGE k " +
            "WHERE k.status = 'PUBLISHED' AND CONTAINS(k.search_text, :query, 1) > 0 " +
            "ORDER BY SCORE(1) DESC",
            nativeQuery = true)
    List<Knowledge> searchPublished(@Param("query") String query);
}