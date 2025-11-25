package dev.knalis.vleapi.model.dto.group;

import lombok.Data;

@Data
public class GroupStudentDto {
    private Long id;
    private String username;
    private String fio;
    private String avatarUrl;
}
