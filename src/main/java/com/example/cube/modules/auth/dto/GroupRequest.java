package com.example.cube.modules.auth.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GroupRequest {

    @NotBlank
    private String groupCode;

    @NotBlank
    private String groupName;

    private String description;
}
