package com.example.cube.modules.auth.repository;

import com.example.cube.modules.auth.entity.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {
    Optional<UserMaster> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
