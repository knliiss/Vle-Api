package dev.knalis.vleapi.controller.version.v1.impl;


import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.impl.CourseMapper;
import dev.knalis.vleapi.mapper.impl.UserMapper;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.course.CourseDto;
import dev.knalis.vleapi.model.dto.user.CreateUserRequest;
import dev.knalis.vleapi.model.dto.user.UpdateUserRequest;
import dev.knalis.vleapi.model.dto.user.UserDto;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.UserService;
import dev.knalis.vleapi.util.ObjectBinder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(UserController.USER_REST_URL)
public class UserController extends AbstractCRUDController<User, UserDto, CreateUserRequest, UpdateUserRequest, Long> {

    public static final String USER_REST_URL = "/api/v1/users";

    private final UserService userService;
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;
    private final ObjectBinder objectBinder;

    @Override
    protected CRUDService<User, Long> getService() {
        return userService;
    }

    @Override
    protected ObjectMapper<User, UserDto, CreateUserRequest, UpdateUserRequest> getMapper() {
        return userMapper;
    }

    @Override
    protected String getRestUrl() {
        return USER_REST_URL;
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User user = userService.findById(id);
        Long groupId = user.getGroup().getId();
        objectBinder.unbindUserFromGroup(id, groupId);

        return super.delete(id);
    }

    @GetMapping("/me/courses")
    public ResponseEntity<List<CourseDto>> getCourses(Authentication auth) {
        String username = auth.getName();
        User user = userService.findByUsername(username);
        Long id = user.getId();
        return getCoursesByUserId(id);
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDto>> getCoursesByUserId(@PathVariable Long id) {
        List<Course> courses;

        courses = userService.findAvailableCoursesForUser(id);

        if (courses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<CourseDto> courseDtos = courses.stream()
                .map(courseMapper::toDto)
                .toList();
        return ResponseEntity.ok(courseDtos);
    }

}
