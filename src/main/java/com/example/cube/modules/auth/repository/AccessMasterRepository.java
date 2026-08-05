package com.example.cube.modules.auth.repository;

import com.example.cube.modules.auth.entity.AccessMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccessMasterRepository extends JpaRepository<AccessMaster, Long> {
    Optional<AccessMaster> findByModuleIdAndAccessCode(Long moduleId, String accessCode);
}
