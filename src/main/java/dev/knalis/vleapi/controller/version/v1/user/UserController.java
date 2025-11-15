package dev.knalis.vleapi.controller.version.v1.user;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.user.UserCreateRequest;
import dev.knalis.vleapi.model.dto.user.UserDto;
import dev.knalis.vleapi.model.dto.user.UserUpdateRequest;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.UserService;
import dev.knalis.vleapi.mapper.impl.UserEntityMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import dev.knalis.vleapi.model.entity.Course;

import java.util.List;

@Tag(name = "Users", description = "Operations for user management")
@RestController
@RequestMapping("/api/v1/users")
public class UserController extends AbstractCRUDController<User, UserDto, UserCreateRequest, UserUpdateRequest, Long> {

    @Autowired
    private UserService userService;

    @Autowired
    private UserEntityMapper userMapper;

    @Override
    protected CRUDService<User, Long> getService() {
        return userService;
    }

    @Override
    protected ObjectMapper<User, UserDto, UserCreateRequest, UserUpdateRequest> getMapper() {
        return userMapper;
    }

    @Override
    protected String getRestUrl() {
        return "users";
    }

    @Operation(summary = "Get available courses for a user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of courses", content = @Content(mediaType = "application/json", schema = @Schema(implementation = dev.knalis.vleapi.model.dto.course.CourseDto.class)))
    @GetMapping("/{id}/courses")
    public ResponseEntity<List<Course>> getAvailableCourses(@PathVariable Long id) {
        List<Course> courses = userService.findAvailableCoursesForUser(id);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Create a new user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Override
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateRequest request) {
        return super.create(request);
    }

    @Operation(summary = "Update a user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @PreAuthorize("hasRole('ADMINISTRATOR') or principal.username == @userService.findById(#id).username")
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return super.update(id, request);
    }

    @Operation(summary = "Delete a user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "User deleted")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }

}
