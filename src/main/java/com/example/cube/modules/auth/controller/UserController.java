package com.example.cube.modules.auth.controller;

import com.example.cube.modules.auth.dto.*;
import com.example.cube.modules.auth.entity.GroupMaster;
import com.example.cube.modules.auth.entity.UserMaster;
import com.example.cube.modules.auth.repository.GroupMasterRepository;
import com.example.cube.modules.auth.repository.UserGroupRepository;
import com.example.cube.modules.auth.repository.UserMasterRepository;
import com.example.cube.modules.auth.service.UserService;
import com.example.cube.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMasterRepository userMasterRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupMasterRepository groupMasterRepository;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {
        UserMaster user = userMasterRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String groupCode = userGroupRepository.findByUserId(user.getUserId())
                .flatMap(ug -> groupMasterRepository.findById(ug.getGroupId()))
                .map(GroupMaster::getGroupCode)
                .orElse(null);

        return ResponseEntity.ok(MeResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .groupCode(groupCode)
                .build());
    }

    // ---- admin endpoints, gated by AUTH/MANAGE_USERS via AccessControlService ----

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request, Authentication auth) {
        return ResponseEntity.ok(userService.create(request, auth));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> list(Pageable pageable, Authentication auth) {
        return ResponseEntity.ok(userService.list(auth, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(userService.getById(id, auth));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody UserStatusUpdateRequest request,
                                                       Authentication auth) {
        return ResponseEntity.ok(userService.updateStatus(id, request.getStatus(), auth));
    }

    @PutMapping("/{id}/group")
    public ResponseEntity<UserResponse> assignGroup(@PathVariable Long id,
                                                      @Valid @RequestBody AssignGroupRequest request,
                                                      Authentication auth) {
        return ResponseEntity.ok(userService.assignGroup(id, request.getGroupId(), auth));
    }
}
