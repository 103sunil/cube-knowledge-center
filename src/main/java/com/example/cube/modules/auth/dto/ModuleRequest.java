package com.example.cube.modules.auth.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ModuleRequest {

    @NotBlank
    private String moduleCode;

    private String moduleName;

    private String description;
}
