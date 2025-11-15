package dev.knalis.vleapi.model.dto.user;

import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserUpdateRequest {
    @Schema(example = "https://example.com/avatar.png")
    private String avatarUrl;

    @Size(min = 6, max = 100)
    @Schema(description = "New password (optional) - plain text; will be hashed")
    private String password;

    @Schema(example = "1", description = "Optional group id to assign the user to")
    private Long groupId;

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
}
