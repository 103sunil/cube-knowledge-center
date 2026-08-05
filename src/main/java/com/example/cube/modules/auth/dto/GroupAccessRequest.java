package com.example.cube.modules.auth.dto;

import lombok.*;
import javax.validation.constraints.Pattern;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GroupAccessRequest {

    @Pattern(regexp = "Y|N", message = "allowed must be Y or N")
    private String allowed;
}
