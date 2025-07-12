package dev.knalis.vleapi.repo;

import dev.knalis.vleapi.model.entity.Topic;
import org.springframework.data.repository.CrudRepository;

public interface TopicRepo extends CrudRepository<Topic, Long> {
}
