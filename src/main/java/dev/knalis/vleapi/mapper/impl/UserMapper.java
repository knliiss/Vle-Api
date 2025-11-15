package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.model.dto.user.UserCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements ObjectMapper<User, Object, UserCreateRequest, Object> {

    @Override
    public Object toDto(User entity) {
        return null;
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
        return u;
    }

    @Override
    public void updateEntity(User entity, Object updateRequest) {
    }
}
