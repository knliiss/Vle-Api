package dev.knalis.vleapi.controller.version.v1.notification;

import dev.knalis.vleapi.model.dto.NotificationRequest;
import dev.knalis.vleapi.model.dto.NotificationResponse;
import dev.knalis.vleapi.service.intrf.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Notifications", description = "Send and view notifications for teachers")
@RestController
@RequestMapping("/api/v1/notifications/teacher")
public class NotificationController {
    @Autowired private NotificationService notificationService;

    @Operation(summary = "Send notification to teacher", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/{userId}")
    public ResponseEntity<NotificationResponse> send(@PathVariable Long userId, @RequestBody NotificationRequest req) {
        return ResponseEntity.ok(notificationService.sendToTeacher(userId, req));
    }

    @Operation(summary = "Get teacher notification history", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR') or principal.username == @userService.findById(#userId).username")
    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationResponse>> history(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getTeacherNotifications(userId));
    }
}

