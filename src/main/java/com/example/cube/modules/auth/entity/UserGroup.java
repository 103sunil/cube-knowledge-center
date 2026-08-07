package com.example.cube.modules.auth.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "CU_USER_GROUP")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserGroup {

    @Id
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "GROUP_ID", nullable = false)
    private Long groupId;
}
