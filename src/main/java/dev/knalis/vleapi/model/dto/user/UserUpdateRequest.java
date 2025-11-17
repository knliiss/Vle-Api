package dev.knalis.vleapi.model.dto.user;

import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateRequest {
    @Schema(example = "https://example.com/avatar.png")
    private String avatarUrl;

    @Size(min = 6, max = 100)
    @Schema(description = "New password (optional) - plain text; will be hashed")
    private String password;

    @Schema(example = "Іван Іванович", description = "Full name (ФИО)")
    private String fio;
    
}
