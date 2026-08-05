package com.example.cube.modules.auth.repository;

import com.example.cube.modules.auth.entity.UserAccess;
import com.example.cube.modules.auth.entity.UserAccessId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserAccessRepository extends JpaRepository<UserAccess, UserAccessId> {
    Optional<UserAccess> findByUserIdAndAccessId(Long userId, Long accessId);
}
