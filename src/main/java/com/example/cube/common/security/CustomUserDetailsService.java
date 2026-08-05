package com.example.cube.common.security;

import com.example.cube.modules.auth.entity.GroupMaster;
import com.example.cube.modules.auth.entity.UserGroup;
import com.example.cube.modules.auth.entity.UserMaster;
import com.example.cube.modules.auth.repository.GroupMasterRepository;
import com.example.cube.modules.auth.repository.UserGroupRepository;
import com.example.cube.modules.auth.repository.UserMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMasterRepository userMasterRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupMasterRepository groupMasterRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserMaster user = userMasterRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<GrantedAuthority> authorities = resolveAuthorities(user.getUserId());

        return User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(!"A".equals(user.getStatus()))
                .authorities(authorities)
                .build();
    }

    private List<GrantedAuthority> resolveAuthorities(Long userId) {
        Optional<UserGroup> userGroup = userGroupRepository.findByUserId(userId);
        if (!userGroup.isPresent()) {
            return Collections.emptyList();
        }
        Optional<GroupMaster> group = groupMasterRepository.findById(userGroup.get().getGroupId());
        return group.map(g -> Collections.<GrantedAuthority>singletonList(
                        new SimpleGrantedAuthority("ROLE_" + g.getGroupCode())))
                .orElse(Collections.emptyList());
    }
}
