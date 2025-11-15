package dev.knalis.vleapi.controller.version.v1.admin;

import dev.knalis.vleapi.util.ObjectBinder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
        objectBinder.bindUserToGroup(userId, groupId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unbind user from group", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/user/{userId}/group/{groupId}")
    public ResponseEntity<Void> unbindUserFromGroup(@PathVariable Long userId, @PathVariable Long groupId) {
        objectBinder.unbindUserFromGroup(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bind course to group", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/course/{courseId}/group/{groupId}")
    public ResponseEntity<Void> bindCourseToGroup(@PathVariable Long courseId, @PathVariable Long groupId) {
        objectBinder.bindCourseToGroup(courseId, groupId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unbind course from group", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/course/{courseId}/group/{groupId}")
    public ResponseEntity<Void> unbindCourseFromGroup(@PathVariable Long courseId, @PathVariable Long groupId) {
        objectBinder.unbindCourseFromGroup(courseId, groupId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bind topic to course", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/topic/{topicId}/course/{courseId}")
    public ResponseEntity<Void> bindTopicToCourse(@PathVariable Long topicId, @PathVariable Long courseId) {
        objectBinder.bindTopicToCourse(topicId, courseId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Unbind topic from course", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/topic/{topicId}/course/{courseId}")
    public ResponseEntity<Void> unbindTopicFromCourse(@PathVariable Long topicId, @PathVariable Long courseId) {
        objectBinder.unbindTopicFromCourse(topicId, courseId);
        return ResponseEntity.noContent().build();
    }

}
