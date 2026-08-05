package com.example.cube.modules.auth.service;

import com.example.cube.modules.auth.dto.GroupRequest;
import com.example.cube.modules.auth.dto.GroupResponse;
import com.example.cube.modules.auth.entity.GroupMaster;
import com.example.cube.modules.auth.repository.GroupMasterRepository;
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
public class GroupService {

    private static final String MODULE = "AUTH";

    private final GroupMasterRepository groupMasterRepository;
    private final AccessControlService accessControlService;

    @Transactional
    public GroupResponse create(GroupRequest request, Authentication auth) {
        requirePermission(auth, "MANAGE_GROUPS");
        if (groupMasterRepository.findAll().stream().anyMatch(g -> g.getGroupCode().equalsIgnoreCase(request.getGroupCode()))) {
            throw new DuplicateResourceException("Group code already exists: " + request.getGroupCode());
        }
        GroupMaster group = GroupMaster.builder()
                .groupCode(request.getGroupCode())
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .build();
        return toResponse(groupMasterRepository.save(group));
    }

    public List<GroupResponse> list(Authentication auth) {
        requirePermission(auth, "MANAGE_GROUPS");
        return groupMasterRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public GroupResponse getById(Long groupId, Authentication auth) {
        requirePermission(auth, "MANAGE_GROUPS");
        return toResponse(findOrThrow(groupId));
    }

    @Transactional
    public GroupResponse update(Long groupId, GroupRequest request, Authentication auth) {
        requirePermission(auth, "MANAGE_GROUPS");
        GroupMaster group = findOrThrow(groupId);
        group.setGroupName(request.getGroupName());
        group.setDescription(request.getDescription());
        return toResponse(groupMasterRepository.save(group));
    }

    @Transactional
    public void delete(Long groupId, Authentication auth) {
        requirePermission(auth, "MANAGE_GROUPS");
        GroupMaster group = findOrThrow(groupId);
        groupMasterRepository.delete(group);
    }

    private GroupMaster findOrThrow(Long groupId) {
        return groupMasterRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found: " + groupId));
    }

    private void requirePermission(Authentication auth, String accessCode) {
        if (!accessControlService.hasPermission(auth.getName(), MODULE, accessCode)) {
            throw new AccessDeniedAppException("Not authorized to " + accessCode);
        }
    }

    private GroupResponse toResponse(GroupMaster g) {
        return GroupResponse.builder()
                .groupId(g.getGroupId())
                .groupCode(g.getGroupCode())
                .groupName(g.getGroupName())
                .description(g.getDescription())
                .build();
    }
}
