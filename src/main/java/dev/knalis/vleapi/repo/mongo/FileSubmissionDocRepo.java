package dev.knalis.vleapi.repo.mongo;

import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileSubmissionDocRepo extends MongoRepository<FileSubmissionDoc, String> {
    List<FileSubmissionDoc> findByTaskId(Long taskId);
    List<FileSubmissionDoc> findByTaskIdAndUserId(Long taskId, Long userId);
    List<FileSubmissionDoc> findByUserId(Long userId);
    List<FileSubmissionDoc> findByTaskIdIn(List<Long> taskIds);
}
