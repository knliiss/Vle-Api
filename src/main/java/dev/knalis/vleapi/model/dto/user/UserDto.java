package dev.knalis.vleapi.model.dto.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private Long id;
    private String username;
    private String avatarUrl;
    private String role;
    private Long groupId;
    private String fio;
    
}
