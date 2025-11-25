package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.group.GroupDto;
import dev.knalis.vleapi.model.entity.Group;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class GroupEntityMapper implements ObjectMapper<Group, GroupDto, GroupDto, GroupDto> {
    @Override
    public GroupDto toDto(Group entity) {
        if (entity == null) return null;
        GroupDto dto = new GroupDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setYear(entity.getYear());
        return dto;
    }

    @Override
    public Group fromCreateRequest(GroupDto dto) {
        Group g = new Group();
        g.setName(dto.getName());
        if (dto.getYear() != null) g.setYear(dto.getYear());
        else {
            g.setYear(new Date(Instant.now().toEpochMilli()).getYear());
        }
        return g;
    }

    @Override
    public void updateEntity(Group entity, GroupDto updateRequest) {
        if (updateRequest.getName() != null) entity.setName(updateRequest.getName());
        if (updateRequest.getYear() != null) entity.setYear(updateRequest.getYear());
    }
}
