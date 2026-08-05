package com.example.cube.modules.auth.entity;

import lombok.*;
import java.io.Serializable;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GroupAccessId implements Serializable {
    private Long groupId;
    private Long accessId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupAccessId)) return false;
        GroupAccessId that = (GroupAccessId) o;
        return Objects.equals(groupId, that.groupId) && Objects.equals(accessId, that.accessId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, accessId);
    }
}
