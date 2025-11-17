package dev.knalis.vleapi.controller.version.v1.teacher;

import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.intrf.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Teachers", description = "Teacher management and relations")
@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get all courses for a teacher", description = "Returns all courses linked to the teacher", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of courses", content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> listCourses(@PathVariable Long id) {
        User teacher = userService.findById(id);
        return ResponseEntity.ok(userService.findCoursesForTeacher(id));
    }

    @Operation(summary = "Get teacher details", description = "Returns teacher with linked courses", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Teacher details", content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}")
    public ResponseEntity<?> getTeacherDetails(@PathVariable Long id) {
        User teacher = userService.findById(id);
        return ResponseEntity.ok(teacher);
    }
}

