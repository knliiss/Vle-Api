package dev.knalis.vleapi.model.dto.group;

import lombok.Data;

import java.util.List;

@Data
public class GroupDto {

    private Long id;
    private String name;
    private short year;

    private List<Long> courseIds;
    private List<Long> userIds;
}
