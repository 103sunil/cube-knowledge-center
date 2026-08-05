package com.example.cube.modules.auth.entity;

import lombok.*;
import java.io.Serializable;
import java.util.Objects;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserAccessId implements Serializable {
    private Long userId;
    private Long accessId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccessId)) return false;
        UserAccessId that = (UserAccessId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(accessId, that.accessId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, accessId);
    }
}
