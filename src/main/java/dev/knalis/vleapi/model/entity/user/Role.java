package dev.knalis.vleapi.model.entity.user;

import lombok.Getter;

public enum Role {
    ADMINISTRATOR("Administrator"),
    TEACHER("Teacher"),
    STUDENT("Student");

    @Getter
    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }



}
