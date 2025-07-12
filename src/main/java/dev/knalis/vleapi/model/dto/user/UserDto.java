package dev.knalis.vleapi.model.dto.user;

import dev.knalis.vleapi.model.entity.user.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private Role role;

    private Long groupId;
    private List<Long> courseIds;
}
