package dev.knalis.vleapi.repo.mongo;

import dev.knalis.vleapi.model.document.TestSubmissionDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSubmissionDocRepo extends MongoRepository<TestSubmissionDoc, String> {
    List<TestSubmissionDoc> findByTaskId(Long taskId);
    List<TestSubmissionDoc> findByTaskIdAndUserId(Long taskId, Long userId);
    List<TestSubmissionDoc> findByUserId(Long userId);
}

