package com.example.cube.modules.auth.service;

import com.example.cube.modules.auth.dto.UserAccessRequest;
import com.example.cube.modules.auth.dto.UserAccessResponse;
import com.example.cube.modules.auth.entity.UserAccess;
import com.example.cube.modules.auth.repository.AccessMasterRepository;
import com.example.cube.modules.auth.repository.UserAccessRepository;
import com.example.cube.modules.auth.repository.UserMasterRepository;
import com.example.cube.common.exception.AccessDeniedAppException;
import com.example.cube.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAccessService {

    private static final String MODULE = "AUTH";

    private final UserAccessRepository userAccessRepository;
    private final UserMasterRepository userMasterRepository;
    private final AccessMasterRepository accessMasterRepository;
    private final AccessControlService accessControlService;

    @Transactional
    public UserAccessResponse setOverride(Long userId, Long accessId, UserAccessRequest request, Authentication auth) {
        requirePermission(auth, "MANAGE_USER_ACCESS");

        if (!userMasterRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        if (!accessMasterRepository.existsById(accessId)) {
            throw new ResourceNotFoundException("Access code not found: " + accessId);
        }

        UserAccess override = userAccessRepository.findByUserIdAndAccessId(userId, accessId)
                .orElse(UserAccess.builder().userId(userId).accessId(accessId).build());
        override.setAllowed(request.getAllowed());
        override.setExpiryDate(request.getExpiryDate());
        userAccessRepository.save(override);

        return UserAccessResponse.builder()
                .userId(userId)
                .accessId(accessId)
                .allowed(override.getAllowed())
                .expiryDate(override.getExpiryDate())
                .build();
    }

    @Transactional
    public void removeOverride(Long userId, Long accessId, Authentication auth) {
        requirePermission(auth, "MANAGE_USER_ACCESS");
        userAccessRepository.findByUserIdAndAccessId(userId, accessId)
                .ifPresent(userAccessRepository::delete);
    }

    private void requirePermission(Authentication auth, String accessCode) {
        if (!accessControlService.hasPermission(auth.getName(), MODULE, accessCode)) {
            throw new AccessDeniedAppException("Not authorized to " + accessCode);
        }
    }
}
