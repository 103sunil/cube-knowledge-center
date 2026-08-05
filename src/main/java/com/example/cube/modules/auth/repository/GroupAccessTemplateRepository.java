package com.example.cube.modules.auth.repository;

import com.example.cube.modules.auth.entity.GroupAccessTemplate;
import com.example.cube.modules.auth.entity.GroupAccessId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GroupAccessTemplateRepository extends JpaRepository<GroupAccessTemplate, GroupAccessId> {
    Optional<GroupAccessTemplate> findByGroupIdAndAccessId(Long groupId, Long accessId);
    boolean existsByGroupId(Long groupId);
    List<GroupAccessTemplate> findByAccessId(Long accessId);
}