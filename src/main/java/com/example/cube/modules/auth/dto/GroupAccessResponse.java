package com.example.cube.modules.auth.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupAccessResponse {
    private Long groupId;
    private Long accessId;
    private String allowed;
}
