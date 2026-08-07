package com.example.cube.modules.auth.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "CU_MODULE_MASTER")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModuleMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MODULE_ID")
    private Long moduleId;

    @Column(name = "MODULE_CODE", nullable = false, unique = true, length = 50)
    private String moduleCode;

    @Column(name = "MODULE_NAME", length = 100)
    private String moduleName;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;
}
