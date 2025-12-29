package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import dev.knalis.vleapi.model.document.TestSubmissionDoc;
import dev.knalis.vleapi.model.entity.task.SubmissionStatus;
import dev.knalis.vleapi.repo.mongo.FileSubmissionDocRepo;
import dev.knalis.vleapi.repo.mongo.TestSubmissionDocRepo;
import dev.knalis.vleapi.service.amazon.FileUploadService;
import dev.knalis.vleapi.service.intrf.SubmissionService;
import dev.knalis.vleapi.repo.TaskRepo;
import dev.knalis.vleapi.model.entity.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private FileSubmissionDocRepo fileSubmissionDocRepo;

    @Autowired
    private TestSubmissionDocRepo testSubmissionDocRepo;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private TaskRepo taskRepo;

    private boolean isOverdue(Long taskId) {
        return taskRepo.findById(taskId)
                .map(Task::getDueDate)
                .map(d -> d != null && d.before(new java.util.Date()))
                .orElse(false);
    }

    @Override
    public FileSubmissionDoc gradeFileSubmission(String submissionId, Double grade) {
        Optional<FileSubmissionDoc> opt = fileSubmissionDocRepo.findById(submissionId);
        FileSubmissionDoc doc;
        if (opt.isPresent()) {
            doc = opt.get();
            doc.setGrade(grade);
            doc.setStatus(SubmissionStatus.GRADED);
        } else {
            throw new IllegalArgumentException("File submission not found: " + submissionId);
        }
        return fileSubmissionDocRepo.save(doc);
    }

    @Override
    public TestSubmissionDoc gradeTestSubmission(String submissionId, Double grade) {
        Optional<TestSubmissionDoc> opt = testSubmissionDocRepo.findById(submissionId);
        TestSubmissionDoc doc;
        if (opt.isPresent()) {
            doc = opt.get();
            doc.setGrade(grade);
        } else {
            throw new IllegalArgumentException("Test submission not found: " + submissionId);
        }
        return testSubmissionDocRepo.save(doc);
    }

    @Override
    public FileSubmissionDoc submitFile(Long taskId, Long userId, MultipartFile file) {
        String url = fileUploadService.uploadFile(file);
        FileSubmissionDoc doc = new FileSubmissionDoc();
        doc.setTaskId(taskId);
        doc.setUserId(userId);
        doc.setSubmitted(LocalDateTime.now());
        doc.setStatus(isOverdue(taskId) ? SubmissionStatus.OVERDUE : SubmissionStatus.ADDED);
        doc.setContentUrl(url);
        return fileSubmissionDocRepo.save(doc);
    }

    @Override
    public TestSubmissionDoc submitTest(Long taskId, Long userId, String content) {
        TestSubmissionDoc doc = new TestSubmissionDoc();
        doc.setTaskId(taskId);
        doc.setUserId(userId);
        doc.setSubmitted(LocalDateTime.now());
        doc.setContent(content);
        doc.setStatus(isOverdue(taskId) ? SubmissionStatus.OVERDUE : SubmissionStatus.ADDED);
        return testSubmissionDocRepo.save(doc);
    }

    @Override
    public List<FileSubmissionDoc> findFileSubmissionsByUser(Long userId) {
        return fileSubmissionDocRepo.findByUserId(userId);
    }

    @Override
    public List<TestSubmissionDoc> findTestSubmissionsByUser(Long userId) {
        return testSubmissionDocRepo.findByUserId(userId);
    }

    @Override
    public List<FileSubmissionDoc> findFileSubmissionsByTaskAndUser(Long taskId, Long userId) {
        return fileSubmissionDocRepo.findByTaskIdAndUserId(taskId, userId);
    }

    @Override
    public List<TestSubmissionDoc> findTestSubmissionsByTaskAndUser(Long taskId, Long userId) {
        return testSubmissionDocRepo.findByTaskIdAndUserId(taskId, userId);
    }

    @Override
    public Optional<FileSubmissionDoc> findFileSubmissionById(String id) {
        return fileSubmissionDocRepo.findById(id);
    }

    @Override
    public Optional<TestSubmissionDoc> findTestSubmissionById(String id) {
        return testSubmissionDocRepo.findById(id);
    }

    @Override
    public List<FileSubmissionDoc> findFileSubmissionsByCourseId(Long courseId) {
        List<Long> taskIds = StreamSupport.stream(taskRepo.findAll().spliterator(), false)
                .filter(t -> t.getTopic() != null && t.getTopic().getCourse() != null && courseId.equals(t.getTopic().getCourse().getId()))
                .map(Task::getId)
                .collect(Collectors.toList());
        if (taskIds.isEmpty()) return List.of();
        return fileSubmissionDocRepo.findByTaskIdIn(taskIds);
    }

    @Override
    public List<TestSubmissionDoc> findTestSubmissionsByCourseId(Long courseId) {
        List<Long> taskIds = StreamSupport.stream(taskRepo.findAll().spliterator(), false)
                .filter(t -> t.getTopic() != null && t.getTopic().getCourse() != null && courseId.equals(t.getTopic().getCourse().getId()))
                .map(Task::getId)
                .collect(Collectors.toList());
        if (taskIds.isEmpty()) return List.of();
        return testSubmissionDocRepo.findByTaskIdIn(taskIds);
    }

}
