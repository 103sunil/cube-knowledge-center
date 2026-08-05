package com.example.cube.modules.auth.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupResponse {
    private Long groupId;
    private String groupCode;
    private String groupName;
    private String description;
}
