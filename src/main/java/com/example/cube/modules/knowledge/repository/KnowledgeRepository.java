package com.example.cube.modules.knowledge.repository;

import com.example.cube.modules.knowledge.entity.Knowledge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {
    Page<Knowledge> findByStatus(String status, Pageable pageable);
    List<Knowledge> findByKnowledgeIdIn(List<Long> ids);
}
