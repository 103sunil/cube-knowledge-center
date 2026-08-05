package com.example.cube.modules.auth.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "ACCESS_MASTER", uniqueConstraints = @UniqueConstraint(columnNames = {"MODULE_ID", "ACCESS_CODE"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccessMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCESS_ID")
    private Long accessId;

    @Column(name = "MODULE_ID", nullable = false)
    private Long moduleId;

    @Column(name = "ACCESS_CODE", nullable = false, length = 50)
    private String accessCode;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;
}
