package com.example.cube.modules.auth.dto;

import lombok.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserAccessRequest {

    @Pattern(regexp = "Y|N", message = "allowed must be Y or N")
    private String allowed;

    private LocalDate expiryDate;
}
