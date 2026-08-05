package com.example.cube.modules.auth.service;

import com.example.cube.modules.auth.entity.*;
import com.example.cube.modules.auth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Implements the permission resolution flow from the architecture doc:
 * USER_MASTER -> USER_GROUP -> ACCESS_MASTER -> USER_ACCESS override ? : GROUP_ACCESS_TEMPLATE
 */
@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final UserMasterRepository userMasterRepository;
    private final UserGroupRepository userGroupRepository;
    private final AccessMasterRepository accessMasterRepository;
    private final UserAccessRepository userAccessRepository;
    private final GroupAccessTemplateRepository groupAccessTemplateRepository;
    private final ModuleMasterRepository moduleMasterRepository;

    public boolean hasPermission(String username, String moduleCode, String accessCode) {
        Optional<UserMaster> userOpt = userMasterRepository.findByUsername(username);
        if (!userOpt.isPresent()) return false;
        UserMaster user = userOpt.get();

        Optional<ModuleMaster> moduleOpt = moduleMasterRepository.findAll().stream()
                .filter(m -> m.getModuleCode().equals(moduleCode))
                .findFirst();
        if (!moduleOpt.isPresent()) return false;

        Optional<AccessMaster> accessOpt = accessMasterRepository
                .findByModuleIdAndAccessCode(moduleOpt.get().getModuleId(), accessCode);
        if (!accessOpt.isPresent()) return false;

        Long accessId = accessOpt.get().getAccessId();

        // 1. USER_ACCESS override
        Optional<UserAccess> override = userAccessRepository.findByUserIdAndAccessId(user.getUserId(), accessId);
        if (override.isPresent()) {
            UserAccess ua = override.get();
            boolean notExpired = ua.getExpiryDate() == null || !ua.getExpiryDate().isBefore(LocalDate.now());
            return "Y".equals(ua.getAllowed()) && notExpired;
        }

        // 2. Fall back to group template
        Optional<UserGroup> userGroup = userGroupRepository.findByUserId(user.getUserId());
        if (!userGroup.isPresent()) return false;

        return groupAccessTemplateRepository
                .findByGroupIdAndAccessId(userGroup.get().getGroupId(), accessId)
                .map(gat -> "Y".equals(gat.getAllowed()))
                .orElse(false);
    }
}
