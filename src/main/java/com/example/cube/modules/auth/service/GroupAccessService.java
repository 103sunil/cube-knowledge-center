package com.example.cube.modules.auth.service;

import com.example.cube.modules.auth.dto.GroupAccessRequest;
import com.example.cube.modules.auth.dto.GroupAccessResponse;
import com.example.cube.modules.auth.entity.GroupAccessTemplate;
import com.example.cube.modules.auth.repository.AccessMasterRepository;
import com.example.cube.modules.auth.repository.GroupAccessTemplateRepository;
import com.example.cube.modules.auth.repository.GroupMasterRepository;
import com.example.cube.common.exception.AccessDeniedAppException;
import com.example.cube.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupAccessService {

    private static final String MODULE = "AUTH";

    private final GroupAccessTemplateRepository groupAccessTemplateRepository;
    private final GroupMasterRepository groupMasterRepository;
    private final AccessMasterRepository accessMasterRepository;
    private final AccessControlService accessControlService;

    @Transactional
    public GroupAccessResponse setAccess(Long groupId, Long accessId, GroupAccessRequest request, Authentication auth) {
        requirePermission(auth, "MANAGE_GROUP_ACCESS");

        if (!groupMasterRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found: " + groupId);
        }
        if (!accessMasterRepository.existsById(accessId)) {
            throw new ResourceNotFoundException("Access code not found: " + accessId);
        }

        GroupAccessTemplate template = groupAccessTemplateRepository.findByGroupIdAndAccessId(groupId, accessId)
                .orElse(GroupAccessTemplate.builder().groupId(groupId).accessId(accessId).build());
        template.setAllowed(request.getAllowed());
        groupAccessTemplateRepository.save(template);

        return GroupAccessResponse.builder()
                .groupId(groupId)
                .accessId(accessId)
                .allowed(template.getAllowed())
                .build();
    }

    private void requirePermission(Authentication auth, String accessCode) {
        if (!accessControlService.hasPermission(auth.getName(), MODULE, accessCode)) {
            throw new AccessDeniedAppException("Not authorized to " + accessCode);
        }
    }
}
