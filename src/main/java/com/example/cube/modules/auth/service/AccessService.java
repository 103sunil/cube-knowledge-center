package com.example.cube.modules.auth.service;

import com.example.cube.modules.auth.dto.AccessRequest;
import com.example.cube.modules.auth.dto.AccessResponse;
import com.example.cube.modules.auth.entity.AccessMaster;
import com.example.cube.modules.auth.repository.AccessMasterRepository;
import com.example.cube.modules.auth.repository.ModuleMasterRepository;
import com.example.cube.modules.auth.repository.GroupAccessTemplateRepository;
import com.example.cube.modules.auth.repository.UserAccessRepository;
import com.example.cube.common.exception.AccessDeniedAppException;
import com.example.cube.common.exception.DuplicateResourceException;
import com.example.cube.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessService {

    private static final String MODULE = "AUTH";

    private final AccessMasterRepository accessMasterRepository;
    private final ModuleMasterRepository moduleMasterRepository;
    private final GroupAccessTemplateRepository groupAccessTemplateRepository;
    private final UserAccessRepository userAccessRepository;
    private final AccessControlService accessControlService;

    @Transactional
    public AccessResponse create(Long moduleId, AccessRequest request, Authentication auth) {
        requirePermission(auth, "MANAGE_ACCESS");
        if (!moduleMasterRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException("Module not found: " + moduleId);
        }
        if (accessMasterRepository.findByModuleIdAndAccessCode(moduleId, request.getAccessCode()).isPresent()) {
            throw new DuplicateResourceException("Access code already exists for module: " + request.getAccessCode());
        }
        AccessMaster access = AccessMaster.builder()
                .moduleId(moduleId)
                .accessCode(request.getAccessCode())
                .description(request.getDescription())
                .build();
        return toResponse(accessMasterRepository.save(access));
    }

    public List<AccessResponse> listByModule(Long moduleId, Authentication auth) {
        requirePermission(auth, "MANAGE_ACCESS");
        return accessMasterRepository.findAll().stream()
                .filter(a -> a.getModuleId().equals(moduleId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long accessId, Authentication auth) {
        requirePermission(auth, "MANAGE_ACCESS");
        AccessMaster access = accessMasterRepository.findById(accessId)
                .orElseThrow(() -> new ResourceNotFoundException("Access code not found: " + accessId));

        // No FK to cascade this at the DB level - clean up manually so nothing
        // orphans in GROUP_ACCESS_TEMPLATE / USER_ACCESS pointing at a dead access_id
        groupAccessTemplateRepository.deleteAll(groupAccessTemplateRepository.findByAccessId(accessId));
        userAccessRepository.deleteAll(userAccessRepository.findByAccessId(accessId));

        accessMasterRepository.delete(access);
    }

    private void requirePermission(Authentication auth, String accessCode) {
        if (!accessControlService.hasPermission(auth.getName(), MODULE, accessCode)) {
            throw new AccessDeniedAppException("Not authorized to " + accessCode);
        }
    }

    private AccessResponse toResponse(AccessMaster a) {
        return AccessResponse.builder()
                .accessId(a.getAccessId())
                .moduleId(a.getModuleId())
                .accessCode(a.getAccessCode())
                .description(a.getDescription())
                .build();
    }
}