package com.example.cube.modules.auth.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccessResponse {
    private Long accessId;
    private Long moduleId;
    private String accessCode;
    private String description;
}
