package com.example.cube.modules.auth.dto;

import lombok.*;
import javax.validation.constraints.Pattern;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserStatusUpdateRequest {

    @Pattern(regexp = "A|I", message = "status must be A or I")
    private String status;
}
