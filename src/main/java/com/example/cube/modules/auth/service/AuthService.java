package com.example.cube.modules.auth.service;

import com.example.cube.modules.auth.dto.LoginRequest;
import com.example.cube.modules.auth.dto.LoginResponse;
import com.example.cube.modules.auth.entity.GroupMaster;
import com.example.cube.modules.auth.entity.UserGroup;
import com.example.cube.modules.auth.entity.UserMaster;
import com.example.cube.common.exception.BadCredentialsAppException;
import com.example.cube.modules.auth.repository.GroupMasterRepository;
import com.example.cube.modules.auth.repository.UserGroupRepository;
import com.example.cube.modules.auth.repository.UserMasterRepository;
import com.example.cube.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMasterRepository userMasterRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupMasterRepository groupMasterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        UserMaster user = userMasterRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsAppException("Invalid username or password"));

        if (!"A".equals(user.getStatus())) {
            throw new BadCredentialsAppException("Account is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsAppException("Invalid username or password");
        }

        List<String> roles = resolveRoles(user.getUserId());
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId(), roles);

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .roles(roles)
                .build();
    }

    private List<String> resolveRoles(Long userId) {
        Optional<UserGroup> userGroup = userGroupRepository.findByUserId(userId);
        if (!userGroup.isPresent()) return Collections.emptyList();
        Optional<GroupMaster> group = groupMasterRepository.findById(userGroup.get().getGroupId());
        return group.map(g -> Collections.singletonList(g.getGroupCode())).orElse(Collections.emptyList());
    }
}
