package dev.knalis.vleapi.service.intrf;

import dev.knalis.vleapi.model.entity.task.TestQuestion;
import java.util.List;

public interface TestQuestionService {
    TestQuestion create(TestQuestion q);
    TestQuestion update(TestQuestion q);
    void delete(Long id);
    List<TestQuestion> listByTask(Long taskId);
    TestQuestion findById(Long id);
}

