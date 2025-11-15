package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.model.entity.task.Task;
import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import dev.knalis.vleapi.model.document.TestSubmissionDoc;
import dev.knalis.vleapi.repo.mongo.FileSubmissionDocRepo;
import dev.knalis.vleapi.repo.mongo.TestSubmissionDocRepo;
import dev.knalis.vleapi.service.intrf.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl extends AbstractCRUDService<Task, Long> implements TaskService {

    @Autowired
    private dev.knalis.vleapi.repo.TaskRepo taskRepo;

    @Autowired
    private FileSubmissionDocRepo fileSubmissionDocRepo;

    @Autowired
    private TestSubmissionDocRepo testSubmissionDocRepo;

    @Override
    CrudRepository<Task, Long> getRepository() {
        return taskRepo;
    }

    @Override
    protected Class<Task> getEntityClass() {
        return Task.class;
    }

    @Override
    public Double getGrade(Long taskId, Long userId) {
        List<FileSubmissionDoc> fileSubs = fileSubmissionDocRepo.findByTaskIdAndUserId(taskId, userId);
        for (FileSubmissionDoc f : fileSubs) {
            if (f.getGrade() != null) return f.getGrade();
        }
        List<TestSubmissionDoc> testSubs = testSubmissionDocRepo.findByTaskIdAndUserId(taskId, userId);
        for (TestSubmissionDoc t : testSubs) {
            if (t.getGrade() != null) return t.getGrade();
        }
        return null;
    }

    @Override
    public Long getId(Task created) {
        return created.getId();
    }

    @Override
    public void delete(Long id) {
        fileSubmissionDocRepo.deleteAll(fileSubmissionDocRepo.findByTaskId(id));
        testSubmissionDocRepo.deleteAll(testSubmissionDocRepo.findByTaskId(id));
        getRepository().deleteById(id);
    }
}
