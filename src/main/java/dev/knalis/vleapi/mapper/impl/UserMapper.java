package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.user.CreateUserRequest;
import dev.knalis.vleapi.model.dto.user.UpdateUserRequest;
import dev.knalis.vleapi.model.dto.user.UserDto;
import dev.knalis.vleapi.model.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements ObjectMapper<User, UserDto, CreateUserRequest, UpdateUserRequest> {

    @Override
    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        if (user.getGroup() != null) {
            dto.setGroupId(user.getGroup().getId());
        }
        dto.setCourseIds(user.getCourseIds());

        return dto;
    }

    @Override
    public User fromCreateRequest(CreateUserRequest dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }

    @Override
    public void updateEntity(User user, UpdateUserRequest dto) {
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null) {
            user.setPassword(dto.getPassword());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
    }
}
