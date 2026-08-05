package com.example.cube.modules.auth.service;

import com.example.cube.modules.auth.dto.UserCreateRequest;
import com.example.cube.modules.auth.dto.UserResponse;
import com.example.cube.modules.auth.entity.GroupMaster;
import com.example.cube.modules.auth.entity.UserGroup;
import com.example.cube.modules.auth.entity.UserMaster;
import com.example.cube.modules.auth.repository.GroupMasterRepository;
import com.example.cube.modules.auth.repository.UserGroupRepository;
import com.example.cube.modules.auth.repository.UserMasterRepository;
import com.example.cube.common.exception.AccessDeniedAppException;
import com.example.cube.common.exception.DuplicateResourceException;
import com.example.cube.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String MODULE = "AUTH";

    private final UserMasterRepository userMasterRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupMasterRepository groupMasterRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessControlService accessControlService;

    @Transactional
    public UserResponse create(UserCreateRequest request, Authentication auth) {
        requirePermission(auth, "MANAGE_USERS");

        if (userMasterRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }
        if (userMasterRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }
        if (!groupMasterRepository.existsById(request.getGroupId())) {
            throw new ResourceNotFoundException("Group not found: " + request.getGroupId());
        }

        UserMaster user = UserMaster.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status("A")
                .build();
        user = userMasterRepository.save(user);

        userGroupRepository.save(UserGroup.builder()
                .userId(user.getUserId())
                .groupId(request.getGroupId())
                .build());

        return toResponse(user);
    }

    public Page<UserResponse> list(Authentication auth, Pageable pageable) {
        requirePermission(auth, "MANAGE_USERS");
        return userMasterRepository.findAll(pageable).map(this::toResponse);
    }

    public UserResponse getById(Long userId, Authentication auth) {
        requirePermission(auth, "MANAGE_USERS");
        return toResponse(findOrThrow(userId));
    }

    @Transactional
    public UserResponse updateStatus(Long userId, String status, Authentication auth) {
        requirePermission(auth, "MANAGE_USERS");
        UserMaster user = findOrThrow(userId);
        user.setStatus(status);
        return toResponse(userMasterRepository.save(user));
    }

    @Transactional
    public UserResponse assignGroup(Long userId, Long groupId, Authentication auth) {
        requirePermission(auth, "MANAGE_USERS");
        UserMaster user = findOrThrow(userId);
        if (!groupMasterRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found: " + groupId);
        }

        UserGroup userGroup = userGroupRepository.findByUserId(userId)
                .orElse(UserGroup.builder().userId(userId).build());
        userGroup.setGroupId(groupId);
        userGroupRepository.save(userGroup);

        return toResponse(user);
    }

    private UserMaster findOrThrow(Long userId) {
        return userMasterRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private void requirePermission(Authentication auth, String accessCode) {
        if (!accessControlService.hasPermission(auth.getName(), MODULE, accessCode)) {
            throw new AccessDeniedAppException("Not authorized to " + accessCode);
        }
    }

    private UserResponse toResponse(UserMaster user) {
        String groupCode = userGroupRepository.findByUserId(user.getUserId())
                .flatMap(ug -> groupMasterRepository.findById(ug.getGroupId()))
                .map(GroupMaster::getGroupCode)
                .orElse(null);

        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .groupCode(groupCode)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
