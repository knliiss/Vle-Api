package dev.knalis.vleapi.service.intrf;

import dev.knalis.vleapi.model.dto.NotificationRequest;
import dev.knalis.vleapi.model.dto.NotificationResponse;
import java.util.List;

public interface NotificationService {
    NotificationResponse sendToTeacher(Long teacherId, NotificationRequest request);
    List<NotificationResponse> getTeacherNotifications(Long teacherId);
}

