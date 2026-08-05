package com.example.cube.modules.auth.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccessResponse {
    private Long userId;
    private Long accessId;
    private String allowed;
    private LocalDate expiryDate;
}
