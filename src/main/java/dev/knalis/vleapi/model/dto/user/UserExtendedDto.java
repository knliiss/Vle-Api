package dev.knalis.vleapi.model.dto.user;

import lombok.Data;

@Data
public class UserExtendedDto {
    private Long id;
    private String username;
    private String avatarUrl;
    private String role;
    private String fio;

    private Long groupId;

    private String academicTitle;
    private String department;
    private String workPhone;
    private String scientificDegree;


}
