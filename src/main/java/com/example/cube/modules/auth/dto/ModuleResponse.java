package com.example.cube.modules.auth.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModuleResponse {
    private Long moduleId;
    private String moduleCode;
    private String moduleName;
    private String description;
}
