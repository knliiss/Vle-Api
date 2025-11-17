package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.model.dto.NotificationRequest;
import dev.knalis.vleapi.model.dto.NotificationResponse;
import dev.knalis.vleapi.model.entity.Notification;
import dev.knalis.vleapi.repo.NotificationRepo;
import dev.knalis.vleapi.service.intrf.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired private NotificationRepo notificationRepo;

    @Override
    public NotificationResponse sendToTeacher(Long teacherId, NotificationRequest request) {
        Notification n = new Notification();
        n.setTeacherId(teacherId);
        n.setSubject(request.getSubject());
        n.setMessage(request.getMessage());
        n.setSentAt(Instant.now());
        n.setStatus("DELIVERED");
        notificationRepo.save(n);
        NotificationResponse resp = new NotificationResponse();
        resp.setId(n.getId());
        resp.setSubject(n.getSubject());
        resp.setMessage(n.getMessage());
        resp.setSentAt(n.getSentAt());
        resp.setStatus(n.getStatus());
        return resp;
    }

    @Override
    public List<NotificationResponse> getTeacherNotifications(Long teacherId) {
        return notificationRepo.findByTeacherIdOrderBySentAtDesc(teacherId)
            .stream().map(n -> {
                NotificationResponse resp = new NotificationResponse();
                resp.setId(n.getId());
                resp.setSubject(n.getSubject());
                resp.setMessage(n.getMessage());
                resp.setSentAt(n.getSentAt());
                resp.setStatus(n.getStatus());
                return resp;
            }).collect(Collectors.toList());
    }
}

