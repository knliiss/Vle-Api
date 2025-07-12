package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.group.CreateGroupRequest;
import dev.knalis.vleapi.model.dto.group.GroupDto;
import dev.knalis.vleapi.model.dto.group.UpdateGroupRequest;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.model.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper implements ObjectMapper<Group, GroupDto, CreateGroupRequest, UpdateGroupRequest> {

    @Override
    public GroupDto toDto(Group group) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setYear(group.getYear());
        dto.setCourseIds(group.getCourses().stream().map(Course::getId).toList());
        dto.setUserIds(group.getUsers().stream().map(User::getId).toList());
        return dto;
    }

    @Override
    public Group fromCreateRequest(CreateGroupRequest request) {
        Group group = new Group();
        group.setName(request.getName());
        group.setYear(request.getYear());
        return group;
    }

    @Override
    public void updateEntity(Group group, UpdateGroupRequest request) {
        if (request.getName() != null) {
            group.setName(request.getName());
        }
        if (request.getYear() != 0) {
            group.setYear(request.getYear());
        }
    }
}
