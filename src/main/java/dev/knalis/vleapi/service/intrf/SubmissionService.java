package dev.knalis.vleapi.service.intrf;

import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import dev.knalis.vleapi.model.document.TestSubmissionDoc;

public interface SubmissionService {

    FileSubmissionDoc gradeFileSubmission(String submissionId, Double grade);

    TestSubmissionDoc gradeTestSubmission(String submissionId, Double grade);

}

