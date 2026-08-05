package com.example.cube.modules.auth.repository;

import com.example.cube.modules.auth.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    Optional<UserGroup> findByUserId(Long userId);
    boolean existsByGroupId(Long groupId);
}