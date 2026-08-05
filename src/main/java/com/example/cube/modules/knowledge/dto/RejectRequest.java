package com.example.cube.modules.knowledge.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RejectRequest {

    @NotBlank
    private String reason;
}
