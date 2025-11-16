package dev.knalis.vleapi.model.dto.user;

import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserUpdateRequest {
    @Schema(example = "https://example.com/avatar.png")
    private String avatarUrl;

    @Size(min = 6, max = 100)
    @Schema(description = "New password (optional) - plain text; will be hashed")
    private String password;

    @Schema(example = "Іван Іванович", description = "Full name (ФИО)")
    private String fio;

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFio() { return fio; }
    public void setFio(String fio) { this.fio = fio; }
}
