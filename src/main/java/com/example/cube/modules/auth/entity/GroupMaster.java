package com.example.cube.modules.auth.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "CU_GROUP_MASTER")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "GROUP_CODE", nullable = false, unique = true, length = 50)
    private String groupCode;

    @Column(name = "GROUP_NAME", nullable = false, length = 100)
    private String groupName;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;
}
