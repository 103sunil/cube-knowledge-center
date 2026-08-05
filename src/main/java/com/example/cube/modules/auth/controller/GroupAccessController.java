package com.example.cube.modules.auth.controller;

import com.example.cube.modules.auth.dto.GroupAccessRequest;
import com.example.cube.modules.auth.dto.GroupAccessResponse;
import com.example.cube.modules.auth.service.GroupAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/access/{accessId}")
@RequiredArgsConstructor
public class GroupAccessController {

    private final GroupAccessService groupAccessService;

    @PutMapping
    public ResponseEntity<GroupAccessResponse> setAccess(@PathVariable Long groupId,
                                                           @PathVariable Long accessId,
                                                           @Valid @RequestBody GroupAccessRequest request,
                                                           Authentication auth) {
        return ResponseEntity.ok(groupAccessService.setAccess(groupId, accessId, request, auth));
    }
}
