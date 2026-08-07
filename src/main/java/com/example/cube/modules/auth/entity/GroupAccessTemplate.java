package com.example.cube.modules.auth.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "CU_GROUP_ACCESS_TEMPLATE")
@IdClass(GroupAccessId.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GroupAccessTemplate {

    @Id
    @Column(name = "GROUP_ID")
    private Long groupId;

    @Id
    @Column(name = "ACCESS_ID")
    private Long accessId;

    @Column(name = "ALLOWED", length = 1)
    private String allowed;
}
