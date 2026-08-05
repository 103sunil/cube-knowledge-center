package com.example.cube.modules.auth.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AccessRequest {

    @NotBlank
    private String accessCode;

    private String description;
}
