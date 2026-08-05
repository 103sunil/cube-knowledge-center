package com.example.cube.modules.auth.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MeResponse {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String groupCode;
}
