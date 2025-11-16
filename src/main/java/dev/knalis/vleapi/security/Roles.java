package dev.knalis.vleapi.security;

public final class Roles {
    private Roles() {}
    public static final String ADMIN = "ADMINISTRATOR";
    public static final String TEACHER = "TEACHER";
    public static final String STUDENT = "STUDENT";

    public static String asAuthority(String role) {
        return "ROLE_" + role;
    }
}

