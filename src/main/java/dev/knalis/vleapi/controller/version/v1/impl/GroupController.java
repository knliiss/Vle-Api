package dev.knalis.vleapi.controller.version.v1.impl;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.impl.GroupMapper;
import dev.knalis.vleapi.mapper.impl.UserMapper;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.group.CreateGroupRequest;
import dev.knalis.vleapi.model.dto.group.GroupDto;
import dev.knalis.vleapi.model.dto.group.UpdateGroupRequest;
import dev.knalis.vleapi.model.dto.user.UserDto;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.util.ObjectBinder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(GroupController.GROUP_REST_URL)
@RequiredArgsConstructor
public class GroupController extends AbstractCRUDController<Group, GroupDto, CreateGroupRequest, UpdateGroupRequest, Long> {

    public static final String GROUP_REST_URL = "/api/v1/groups";
    private final GroupMapper groupMapper;
    private final UserMapper userMapper;
    private final CRUDService<Group, Long> crudService;
    private final ObjectBinder objectBinder;


    @Override
    protected CRUDService<Group, Long> getService() {
        return crudService;
    }

    @Override
    protected ObjectMapper<Group, GroupDto, CreateGroupRequest, UpdateGroupRequest> getMapper() {
        return groupMapper;
    }

    @Override
    protected String getRestUrl() {
        return GROUP_REST_URL;
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Group group = getService().findById(id);
        List<Long> bindedUserIds = group.getUsers().stream().map(User::getId).toList();
        for (Long userId : bindedUserIds) {
            objectBinder.unbindUserFromGroup(userId, id);
        }

        List<Long> bindedCourseIds = group.getCourses().stream().map(Course::getId).toList();
        for (Long courseId : bindedCourseIds) {
            objectBinder.unbindCourseFromGroup(courseId, id);
        }

        return super.delete(id);
    }

    @PutMapping("/{id}/users/{userId}")
    public ResponseEntity<GroupDto> addUserToGroup(@PathVariable Long id, @PathVariable Long userId) {
        objectBinder.bindUserToGroup(userId, id);
        Group group = getService().findById(id);
        return ResponseEntity.ok(getMapper().toDto(group));
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    public ResponseEntity<GroupDto> removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        objectBinder.unbindUserFromGroup(userId, groupId);
        Group group = getService().findById(groupId);
        return ResponseEntity.ok(getMapper().toDto(group));
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<UserDto>> getUsersFromGroup(@PathVariable Long id) {
        Group group = getService().findById(id);
        List<UserDto> userDtos = group.getUsers().stream()
                .map(userMapper::toDto)
                .toList();
        if (userDtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userDtos);
    }


}
