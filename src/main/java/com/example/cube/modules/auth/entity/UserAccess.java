package com.example.cube.modules.auth.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CU_USER_ACCESS")
@IdClass(UserAccessId.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccess {

    @Id
    @Column(name = "USER_ID")
    private Long userId;

    @Id
    @Column(name = "ACCESS_ID")
    private Long accessId;

    @Column(name = "ALLOWED", length = 1)
    private String allowed;

    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;
}
