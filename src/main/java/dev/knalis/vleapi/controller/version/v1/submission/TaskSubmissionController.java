package dev.knalis.vleapi.controller.version.v1.submission;

import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import dev.knalis.vleapi.model.document.TestSubmissionDoc;
import dev.knalis.vleapi.service.intrf.SubmissionService;
import dev.knalis.vleapi.service.intrf.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static dev.knalis.vleapi.security.Spel.HAS_STUDENT;

@Tag(name = "Submissions", description = "Endpoints for submitting tasks and viewing own submissions")
@RestController
@RequestMapping("/api/v1")
public class TaskSubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Submit a file for a task", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "File submitted")
    @PreAuthorize(HAS_STUDENT + " and @accessControl.canSubmitTask(#taskId, principal.username)")
    @PostMapping("/tasks/{taskId}/submissions/file")
    public ResponseEntity<FileSubmissionDoc> submitFile(@PathVariable Long taskId,
                                                        @RequestParam("file") MultipartFile file,
                                                        Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName()).getId();
        FileSubmissionDoc result = submissionService.submitFile(taskId, userId, file);
        return ResponseEntity.status(201).body(result);
    }

    @Operation(summary = "Submit a test (text) answer for a task", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "Test submitted")
    @PreAuthorize(HAS_STUDENT + " and @accessControl.canSubmitTask(#taskId, principal.username)")
    @PostMapping("/tasks/{taskId}/submissions/test")
    public ResponseEntity<TestSubmissionDoc> submitTest(@PathVariable Long taskId,
                                                        @RequestBody String content,
                                                        Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName()).getId();
        TestSubmissionDoc result = submissionService.submitTest(taskId, userId, content);
        return ResponseEntity.status(201).body(result);
    }

    @Operation(summary = "List my file submissions", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of file submissions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileSubmissionDoc.class)))
    @PreAuthorize(HAS_STUDENT + " or hasRole('" + "ADMINISTRATOR" + "') or hasRole('" + "TEACHER" + "')")
    @GetMapping("/users/me/submissions/files")
    public ResponseEntity<List<FileSubmissionDoc>> myFileSubmissions(Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName()).getId();
        List<FileSubmissionDoc> result = submissionService.findFileSubmissionsByUser(userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "List my test submissions", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of test submissions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestSubmissionDoc.class)))
    @PreAuthorize(HAS_STUDENT + " or hasRole('" + "ADMINISTRATOR" + "') or hasRole('" + "TEACHER" + "')")
    @GetMapping("/users/me/submissions/tests")
    public ResponseEntity<List<TestSubmissionDoc>> myTestSubmissions(Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName()).getId();
        List<TestSubmissionDoc> result = submissionService.findTestSubmissionsByUser(userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "List my submissions for a specific task", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of submissions for task by current user")
    @PreAuthorize(HAS_STUDENT + " and @accessControl.canSubmitTask(#taskId, principal.username)")
    @GetMapping("/tasks/{taskId}/submissions/me")
    public ResponseEntity<?> mySubmissionsForTask(@PathVariable Long taskId, Authentication authentication) {
        Long userId = userService.findByUsername(authentication.getName()).getId();
        List<FileSubmissionDoc> files = submissionService.findFileSubmissionsByTaskAndUser(taskId, userId);
        List<TestSubmissionDoc> tests = submissionService.findTestSubmissionsByTaskAndUser(taskId, userId);
        return ResponseEntity.ok(new Object() {
            public final List<FileSubmissionDoc> fileSubmissions = files;
            public final List<TestSubmissionDoc> testSubmissions = tests;
        });
    }

}
