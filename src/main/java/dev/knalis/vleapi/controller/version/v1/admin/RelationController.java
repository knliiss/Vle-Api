package dev.knalis.vleapi.controller.version.v1.admin;

import dev.knalis.vleapi.util.ObjectBinder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Relations", description = "Manage relations between users, groups, courses, topics")
@RestController
@RequestMapping("/api/v1/admin/relations")
public class RelationController {

    @Autowired
    private ObjectBinder objectBinder;

    @Operation(summary = "Bind user to group", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/user/{userId}/group/{groupId}")
    public ResponseEntity<Void> bindUserToGroup(@PathVariable Long userId, @PathVariable Long groupId) {
        try {
            objectBinder.bindUserToGroup(userId, groupId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Unbind user from group", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/user/{userId}/group/{groupId}")
    public ResponseEntity<Void> unbindUserFromGroup(@PathVariable Long userId, @PathVariable Long groupId) {
        try {
            objectBinder.unbindUserFromGroup(userId, groupId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Bind course to group", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/course/{courseId}/group/{groupId}")
    public ResponseEntity<Void> bindCourseToGroup(@PathVariable Long courseId, @PathVariable Long groupId) {
        try {
            objectBinder.bindCourseToGroup(courseId, groupId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Unbind course from group", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/course/{courseId}/group/{groupId}")
    public ResponseEntity<Void> unbindCourseFromGroup(@PathVariable Long courseId, @PathVariable Long groupId) {
        try {
            objectBinder.unbindCourseFromGroup(courseId, groupId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Bind topic to course", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/topic/{topicId}/course/{courseId}")
    public ResponseEntity<Void> bindTopicToCourse(@PathVariable Long topicId, @PathVariable Long courseId) {
        try {
            objectBinder.bindTopicToCourse(topicId, courseId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Unbind topic from course", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/topic/{topicId}/course/{courseId}")
    public ResponseEntity<Void> unbindTopicFromCourse(@PathVariable Long topicId, @PathVariable Long courseId) {
        try {
            objectBinder.unbindTopicFromCourse(topicId, courseId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Bind teacher to course", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Teacher assigned to course", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping(value = "/teacher/{teacherId}/course/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> bindTeacherToCourse(@PathVariable Long teacherId, @PathVariable Long courseId) {
        try {
            objectBinder.bindTeacherToCourse(teacherId, courseId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Unbind teacher from course", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Teacher unassigned from course")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/teacher/{teacherId}/course/{courseId}")
    public ResponseEntity<Void> unbindTeacherFromCourse(@PathVariable Long teacherId, @PathVariable Long courseId) {
        try {
            objectBinder.unbindTeacherFromCourse(teacherId, courseId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
