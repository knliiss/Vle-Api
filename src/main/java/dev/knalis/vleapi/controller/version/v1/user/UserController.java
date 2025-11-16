package dev.knalis.vleapi.controller.version.v1.user;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.user.UserCreateRequest;
import dev.knalis.vleapi.model.dto.user.UserDto;
import dev.knalis.vleapi.model.dto.user.UserUpdateRequest;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.security.Roles;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.UserService;
import dev.knalis.vleapi.mapper.impl.UserEntityMapper;
import dev.knalis.vleapi.model.dto.user.UserExtendedDto;
import dev.knalis.vleapi.service.impl.UserProfileAssembler;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import dev.knalis.vleapi.model.entity.Course;

import java.util.List;

import static dev.knalis.vleapi.security.Spel.*;

@Tag(name = "Users", description = "Operations for user management")
@RestController
@RequestMapping("/api/v1/users")
public class UserController extends AbstractCRUDController<User, UserDto, UserCreateRequest, UserUpdateRequest, Long> {

    @Autowired
    private UserService userService;

    @Autowired
    private UserEntityMapper userMapper;

    @Autowired
    private UserProfileAssembler profileAssembler;
    
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
    @PreAuthorize(HAS_ADMIN + " or hasRole('" + Roles.TEACHER + "') or " + IS_SELF_BY_PATH_ID)
    @GetMapping("/{id}/courses")
    public ResponseEntity<List<Course>> getAvailableCourses(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(null);
        }
        List<Course> courses = userService.findAvailableCoursesForUser(id);
        if (courses == null || courses.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Create a new user", description = "Creates a user. Group assignment is optional.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class), examples = @ExampleObject(value = "{\n  \"username\": \"johndoe\",\n  \"password\": \"P@ssw0rd\",\n  \"role\": \"STUDENT\"\n}")))
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class), examples = @ExampleObject(value = "{\n  \"type\": \"https://http.dev/problems/validation-error\",\n  \"title\": \"Validation failed\",\n  \"status\": 400,\n  \"detail\": \"Invalid payload\"\n}")))
    @PreAuthorize(HAS_ADMIN)
    @Override
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateRequest request) {
        return super.create(request);
    }

    @Operation(summary = "Update a user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    @PreAuthorize(HAS_ADMIN + " or " + IS_SELF_BY_PATH_ID)
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return super.update(id, request);
    }

    @Operation(summary = "Partially update user", description = "Updates only provided fields. PATCH is preferred for partial updates.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class), examples = @ExampleObject(value = "{\n  \"avatarUrl\": \"https://.../avatar.png\"\n}")))
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class), examples = @ExampleObject(value = "{\n  \"type\": \"https://http.dev/problems/validation-error\",\n  \"title\": \"Validation failed\",\n  \"status\": 400,\n  \"detail\": \"Invalid payload\"\n}")))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class), examples = @ExampleObject(value = "{\n  \"type\": \"https://http.dev/problems/user-not-found\",\n  \"title\": \"User not found\",\n  \"status\": 404,\n  \"detail\": \"User not found\"\n}")))
    @PreAuthorize(HAS_ADMIN + " or " + IS_SELF_BY_PATH_ID)
    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> patch(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        UserDto updated = super.patch(id, request).getBody();
        if (updated == null) return ResponseEntity.status(404).body(null);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "User deleted")
    @PreAuthorize(HAS_ADMIN)
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }

    @Operation(summary = "List users", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of users")
    @PreAuthorize(HAS_ADMIN)
    @Override
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return super.findAll();
    }

    @Operation(summary = "Get user by id (extended)", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize(HAS_ADMIN + " or " + IS_SELF_BY_PATH_ID)
    @GetMapping("/{id}/extended")
    public ResponseEntity<UserExtendedDto> getExtendedById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(profileAssembler.assemble(user));
    }

    @Operation(
            summary = "Get current authenticated user (extended)",
            description = "Returns extended profile info for current user",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Current authenticated user's extended profile", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserExtendedDto.class), examples = @ExampleObject(value = "{\n  \"id\": 1,\n  \"username\": \"johndoe\",\n  \"fio\": \"John Doe\",\n  \"role\": \"STUDENT\",\n  \"studentProfile\": { \"groupId\": 10 }\n}")))
    @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class), examples = @ExampleObject(value = "{\"type\": \"https://http.dev/problems/unauthorized\",\"title\": \"Not authenticated\",\"status\": 401,\"detail\": \"No authentication provided\"}")))
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body(ProblemDetail.forStatus(401));
        }
        User user = userService.findByUsername(authentication.getName());
        return ResponseEntity.ok(profileAssembler.assemble(user));
    }
}
