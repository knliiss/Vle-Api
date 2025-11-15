package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import dev.knalis.vleapi.model.document.TestSubmissionDoc;
import dev.knalis.vleapi.repo.mongo.FileSubmissionDocRepo;
import dev.knalis.vleapi.repo.mongo.TestSubmissionDocRepo;
import dev.knalis.vleapi.service.intrf.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private FileSubmissionDocRepo fileSubmissionDocRepo;

    @Autowired
    private TestSubmissionDocRepo testSubmissionDocRepo;

    @Override
    public FileSubmissionDoc gradeFileSubmission(String submissionId, Double grade) {
        Optional<FileSubmissionDoc> opt = fileSubmissionDocRepo.findById(submissionId);
        FileSubmissionDoc doc;
        if (opt.isPresent()) {
            doc = opt.get();
            doc.setGrade(grade);
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
}

