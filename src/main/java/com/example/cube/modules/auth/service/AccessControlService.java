package com.example.cube.modules.auth.service;

import com.example.cube.modules.auth.dto.PermissionResponse;
import com.example.cube.modules.auth.entity.*;
import com.example.cube.modules.auth.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        Optional<UserAccess> override = userAccessRepository.findByUserIdAndAccessId(user.getUserId(), accessId);
        if (override.isPresent()) {
            UserAccess ua = override.get();
            boolean notExpired = ua.getExpiryDate() == null || !ua.getExpiryDate().isBefore(LocalDate.now());
            return "Y".equals(ua.getAllowed()) && notExpired;
        }

        Optional<UserGroup> userGroup = userGroupRepository.findByUserId(user.getUserId());
        if (!userGroup.isPresent()) return false;

        return groupAccessTemplateRepository
                .findByGroupIdAndAccessId(userGroup.get().getGroupId(), accessId)
                .map(gat -> "Y".equals(gat.getAllowed()))
                .orElse(false);
    }

    /**
     * Enumerates every access code the user currently has, via the same
     * override-then-template resolution as hasPermission() above - used by
     * GET /api/v1/users/me/permissions so a frontend can hide/show UI without
     * discovering a 403 by trial and error.
     */
    public List<PermissionResponse> resolveAllPermissions(String username) {
        List<ModuleMaster> modules = moduleMasterRepository.findAll();

        return accessMasterRepository.findAll().stream()
                .filter(access -> {
                    String moduleCode = modules.stream()
                            .filter(m -> m.getModuleId().equals(access.getModuleId()))
                            .map(ModuleMaster::getModuleCode)
                            .findFirst()
                            .orElse(null);
                    return moduleCode != null && hasPermission(username, moduleCode, access.getAccessCode());
                })
                .map(access -> {
                    String moduleCode = modules.stream()
                            .filter(m -> m.getModuleId().equals(access.getModuleId()))
                            .map(ModuleMaster::getModuleCode)
                            .findFirst()
                            .orElse(null);
                    return PermissionResponse.builder()
                            .moduleCode(moduleCode)
                            .accessCode(access.getAccessCode())
                            .build();
                })
                .collect(Collectors.toList());
    }
}