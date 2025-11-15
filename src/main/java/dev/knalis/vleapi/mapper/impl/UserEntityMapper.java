package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.user.UserCreateRequest;
import dev.knalis.vleapi.model.dto.user.UserUpdateRequest;
import dev.knalis.vleapi.model.dto.user.UserDto;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.model.entity.Group;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper implements ObjectMapper<User, UserDto, UserCreateRequest, UserUpdateRequest> {

    @Override
    public UserDto toDto(User entity) {
        if (entity == null) return null;
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setAvatarUrl(entity.getAvatarUrl());
        dto.setRole(entity.getRole() == null ? null : entity.getRole().name());
        dto.setGroupId(entity.getGroup() == null ? null : entity.getGroup().getId());
        return dto;
    }

    @Override
    public User fromCreateRequest(UserCreateRequest dto) {
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        if (dto.getRole() != null) {
            u.setRole(dev.knalis.vleapi.model.entity.user.Role.fromString(dto.getRole()));
        } else {
            u.setRole(dev.knalis.vleapi.model.entity.user.Role.STUDENT);
        }
        if (dto.getGroupId() != null) {
            Group g = new Group(); g.setId(dto.getGroupId()); u.setGroup(g);
        }
        return u;
    }

    @Override
    public void updateEntity(User entity, UserUpdateRequest updateRequest) {
        if (updateRequest.getAvatarUrl() != null) entity.setAvatarUrl(updateRequest.getAvatarUrl());
        if (updateRequest.getPassword() != null) entity.setPassword(updateRequest.getPassword());
        if (updateRequest.getGroupId() != null) { Group g = new Group(); g.setId(updateRequest.getGroupId()); entity.setGroup(g); }
    }
}

