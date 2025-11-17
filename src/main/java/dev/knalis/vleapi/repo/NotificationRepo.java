package dev.knalis.vleapi.repo;

import dev.knalis.vleapi.model.entity.Notification;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface NotificationRepo extends CrudRepository<Notification, Long> {
    List<Notification> findByTeacherIdOrderBySentAtDesc(Long teacherId);
}

