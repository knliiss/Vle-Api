package dev.knalis.vleapi.service.intrf;

import dev.knalis.vleapi.model.entity.task.Task;

public interface TaskService extends CRUDService<Task, Long> {
    Double getGrade(Long taskId, Long userId);
}
