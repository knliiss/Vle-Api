package dev.knalis.vleapi.repo;

import dev.knalis.vleapi.model.entity.task.TestQuestion;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface TestQuestionRepo extends CrudRepository<TestQuestion, Long> {
    List<TestQuestion> findByTaskIdOrderByOrderAsc(Long taskId);
    void deleteByTaskId(Long taskId);
}

