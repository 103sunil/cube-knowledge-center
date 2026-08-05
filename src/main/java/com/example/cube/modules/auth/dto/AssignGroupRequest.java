package com.example.cube.modules.auth.dto;

import lombok.*;
import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AssignGroupRequest {

    @NotNull
    private Long groupId;
}
