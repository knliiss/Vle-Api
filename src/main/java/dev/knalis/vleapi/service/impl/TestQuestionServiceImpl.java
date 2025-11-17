package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.model.entity.task.TestQuestion;
import dev.knalis.vleapi.repo.TestQuestionRepo;
import dev.knalis.vleapi.service.intrf.TestQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestQuestionServiceImpl implements TestQuestionService {

    @Autowired
    private TestQuestionRepo repo;

    @Override
    public TestQuestion create(TestQuestion q) { return repo.save(q); }

    @Override
    public TestQuestion update(TestQuestion q) { return repo.save(q); }

    @Override
    public void delete(Long id) { repo.deleteById(id); }

    @Override
    public List<TestQuestion> listByTask(Long taskId) { return repo.findByTaskIdOrderByOrderAsc(taskId); }

    @Override
    public TestQuestion findById(Long id) { return repo.findById(id).orElseThrow(); }
}

