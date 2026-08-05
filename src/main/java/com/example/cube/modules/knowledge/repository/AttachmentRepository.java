package com.example.cube.modules.knowledge.repository;

import com.example.cube.modules.knowledge.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByKnowledgeId(Long knowledgeId);
}
