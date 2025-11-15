package dev.knalis.vleapi.repo;

import dev.knalis.vleapi.model.entity.task.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepo extends CrudRepository<Task, Long> {
}
