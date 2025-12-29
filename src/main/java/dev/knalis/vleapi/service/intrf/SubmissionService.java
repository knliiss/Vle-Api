package dev.knalis.vleapi.service.intrf;

import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import dev.knalis.vleapi.model.document.TestSubmissionDoc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface SubmissionService {

    FileSubmissionDoc gradeFileSubmission(String submissionId, Double grade);

    TestSubmissionDoc gradeTestSubmission(String submissionId, Double grade);

    FileSubmissionDoc submitFile(Long taskId, Long userId, MultipartFile file);

    TestSubmissionDoc submitTest(Long taskId, Long userId, String content);

    List<FileSubmissionDoc> findFileSubmissionsByUser(Long userId);

    List<TestSubmissionDoc> findTestSubmissionsByUser(Long userId);

    List<FileSubmissionDoc> findFileSubmissionsByTaskAndUser(Long taskId, Long userId);

    List<TestSubmissionDoc> findTestSubmissionsByTaskAndUser(Long taskId, Long userId);

    Optional<FileSubmissionDoc> findFileSubmissionById(String id);

    Optional<TestSubmissionDoc> findTestSubmissionById(String id);

    List<FileSubmissionDoc> findFileSubmissionsByCourseId(Long courseId);

    List<TestSubmissionDoc> findTestSubmissionsByCourseId(Long courseId);
}
