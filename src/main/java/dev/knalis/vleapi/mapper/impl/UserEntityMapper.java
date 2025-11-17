package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.user.UserCreateRequest;
import dev.knalis.vleapi.model.dto.user.UserUpdateRequest;
import dev.knalis.vleapi.model.dto.user.UserDto;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.model.entity.user.Role;
import dev.knalis.vleapi.repo.StudentProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper implements ObjectMapper<User, UserDto, UserCreateRequest, UserUpdateRequest> {

    @Autowired
    private StudentProfileRepo studentProfileRepo;

    @Override
    public UserDto toDto(User entity) {
        if (entity == null) return null;
        UserDto dto = new UserDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setAvatarUrl(entity.getAvatarUrl());
        dto.setRole(entity.getRole() == null ? null : entity.getRole().name());
        dto.setFio(entity.getFio());
        if (entity.getRole() == Role.STUDENT) {
            studentProfileRepo.findByUserId(entity.getId())
                    .ifPresent(sp -> {
                        if (sp.getGroup() != null) {
                            dto.setGroupId(sp.getGroup().getId());
                        }
                    });
        }
        return dto;
    }

    @Override
    public User fromCreateRequest(UserCreateRequest dto) {
        User u = new User();
        if (dto.getUsername() != null) u.setUsername(dto.getUsername().trim().toLowerCase());
        u.setPassword(dto.getPassword());
        u.setFio(dto.getFio());
        if (dto.getRole() != null) {
            u.setRole(Role.fromString(dto.getRole()));
        } else {
            u.setRole(Role.STUDENT);
        }
        return u;
    }

    @Override
    public void updateEntity(User entity, UserUpdateRequest updateRequest) {
        if (updateRequest.getAvatarUrl() != null) entity.setAvatarUrl(updateRequest.getAvatarUrl());
        if (updateRequest.getPassword() != null) entity.setPassword(updateRequest.getPassword());
        if (updateRequest.getFio() != null) entity.setFio(updateRequest.getFio());
    }
}
