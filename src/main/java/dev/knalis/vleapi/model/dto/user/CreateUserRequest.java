package dev.knalis.vleapi.model.dto.user;

import dev.knalis.vleapi.model.entity.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$", message = "Username must be at least 3 characters long and can only contain letters, numbers, dots, underscores, and hyphens.")
    private String username;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{6,}$", message = "Password must be at least 6 characters long and can contain letters, numbers, and special characters.")
    private String password;

    @NotNull
    private Role role;
}