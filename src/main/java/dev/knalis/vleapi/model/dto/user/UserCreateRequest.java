package dev.knalis.vleapi.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    @Schema(example = "johndoe", description = "Unique username")
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(example = "P@ssw0rd", description = "User password (plain text). Will be hashed before storing")
    private String password;

    @Schema(example = "STUDENT", description = "Role of the user (ADMINISTRATOR, TEACHER, STUDENT)")
    private String role;

    @Schema(example = "1", description = "Optional group id to assign the user to")
    private Long groupId;
    
}
