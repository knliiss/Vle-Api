package dev.knalis.vleapi.controller.version.v1.submission;

import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import dev.knalis.vleapi.model.document.TestSubmissionDoc;
import dev.knalis.vleapi.model.dto.submission.TestSubmitRequest;
import dev.knalis.vleapi.repo.mongo.FileSubmissionDocRepo;
import dev.knalis.vleapi.repo.mongo.TestSubmissionDocRepo;
import dev.knalis.vleapi.security.AccessControl;
import dev.knalis.vleapi.service.intrf.SubmissionService;
import dev.knalis.vleapi.service.intrf.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Tag(name = "Submissions (v1)", description = "Endpoints for student submissions (test & file) and grading")
@RestController
@RequestMapping("/api/v1/submissions-ext")
class SubmissionV1Controller {

    @Autowired private SubmissionService submissionService;
    @Autowired private UserService userService;
    @Autowired private FileSubmissionDocRepo fileSubmissionDocRepo;
    @Autowired private TestSubmissionDocRepo testSubmissionDocRepo;
    @Autowired private AccessControl accessControl;

    @Operation(summary = "Submit a test (text) answer for a task", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('STUDENT') and @accessControl.canSubmitTask(#taskId, principal.username)")
    @PostMapping("/test/{taskId}")
    public ResponseEntity<TestSubmissionDoc> submitTest(@PathVariable Long taskId, @RequestBody TestSubmitRequest req, Authentication auth) {
        Long userId = userService.findByUsername(auth.getName()).getId();
        TestSubmissionDoc doc = submissionService.submitTest(taskId, userId, req.getContent());
        return ResponseEntity.ok(doc);
    }

    @Operation(summary = "Submit a file for a task (multipart form)", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('STUDENT') and @accessControl.canSubmitTask(#taskId, principal.username)")
    @PostMapping("/file/{taskId}")
    public ResponseEntity<FileSubmissionDoc> submitFile(@PathVariable Long taskId, @RequestParam("file") MultipartFile file, Authentication auth) {
        Long userId = userService.findByUsername(auth.getName()).getId();
        FileSubmissionDoc doc = submissionService.submitFile(taskId, userId, file);
        return ResponseEntity.ok(doc);
    }

    @Operation(summary = "Get current student's file submissions", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/files/me")
    public ResponseEntity<List<FileSubmissionDoc>> myFileSubmissions(Authentication auth) {
        Long userId = userService.findByUsername(auth.getName()).getId();
        return ResponseEntity.ok(submissionService.findFileSubmissionsByUser(userId));
    }

    @Operation(summary = "Get current student's test submissions", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/tests/me")
    public ResponseEntity<List<TestSubmissionDoc>> myTestSubmissions(Authentication auth) {
        Long userId = userService.findByUsername(auth.getName()).getId();
        return ResponseEntity.ok(submissionService.findTestSubmissionsByUser(userId));
    }

    @Operation(summary = "Teacher/Admin grade a file submission", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('TEACHER')")
    @PostMapping("/file/{submissionId}/grade")
    public ResponseEntity<?> gradeFile(@PathVariable String submissionId, @RequestParam Double grade, Authentication auth) {
        Optional<FileSubmissionDoc> opt = fileSubmissionDocRepo.findById(submissionId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        FileSubmissionDoc submission = opt.get();
        Long taskId = submission.getTaskId();
        String username = auth.getName();
        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().contains("ADMINISTRATOR"))) {
            if (!accessControl.canManageTask(taskId, username)) {
                return ResponseEntity.status(403).body("Forbidden");
            }
        }
        FileSubmissionDoc updated = submissionService.gradeFileSubmission(submissionId, grade);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Teacher/Admin grade a test submission", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('TEACHER')")
    @PostMapping("/test/{submissionId}/grade")
    public ResponseEntity<?> gradeTest(@PathVariable String submissionId, @RequestParam Double grade, Authentication auth) {
        Optional<TestSubmissionDoc> opt = testSubmissionDocRepo.findById(submissionId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        TestSubmissionDoc submission = opt.get();
        Long taskId = submission.getTaskId();
        String username = auth.getName();
        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().contains("ADMINISTRATOR"))) {
            if (!accessControl.canManageTask(taskId, username)) {
                return ResponseEntity.status(403).body("Forbidden");
            }
        }
        TestSubmissionDoc updated = submissionService.gradeTestSubmission(submissionId, grade);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Download file submission (redirect to S3/public URL)", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('TEACHER') or (hasRole('STUDENT') and @userService.findByUsername(principal.username).id == @submissionService.findFileSubmissionById(#submissionId).orElse(null).userId)")
    @GetMapping("/file/{submissionId}/download")
    public ResponseEntity<?> downloadFileSubmission(@PathVariable String submissionId, Authentication auth) {
        Optional<FileSubmissionDoc> opt = submissionService.findFileSubmissionById(submissionId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        FileSubmissionDoc doc = opt.get();
        Long taskId = doc.getTaskId();
        String username = auth.getName();
        // teachers/admins may download if they manage the task
        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().contains("ADMINISTRATOR"))) {
            if (!accessControl.canManageTask(taskId, username) && !username.equals(userService.findById(doc.getUserId()).getUsername())) {
                return ResponseEntity.status(403).body("Forbidden");
            }
        }
        // Return redirect to file URL
        return ResponseEntity.status(302).header("Location", doc.getContentUrl()).build();
    }
}
