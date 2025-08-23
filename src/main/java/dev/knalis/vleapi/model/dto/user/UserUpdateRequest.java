package dev.knalis.vleapi.model.dto.user;

import dev.knalis.vleapi.model.entity.user.Role;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private String username;

    private String password;

    private String avatarUrl;

    private Role role;

}
