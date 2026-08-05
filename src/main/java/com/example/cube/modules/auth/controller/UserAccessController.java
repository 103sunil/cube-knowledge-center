package com.example.cube.modules.auth.controller;

import com.example.cube.modules.auth.dto.UserAccessRequest;
import com.example.cube.modules.auth.dto.UserAccessResponse;
import com.example.cube.modules.auth.service.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users/{userId}/access/{accessId}")
@RequiredArgsConstructor
public class UserAccessController {

    private final UserAccessService userAccessService;

    @PutMapping
    public ResponseEntity<UserAccessResponse> setOverride(@PathVariable Long userId,
                                                            @PathVariable Long accessId,
                                                            @Valid @RequestBody UserAccessRequest request,
                                                            Authentication auth) {
        return ResponseEntity.ok(userAccessService.setOverride(userId, accessId, request, auth));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeOverride(@PathVariable Long userId,
                                                @PathVariable Long accessId,
                                                Authentication auth) {
        userAccessService.removeOverride(userId, accessId, auth);
        return ResponseEntity.noContent().build();
    }
}
