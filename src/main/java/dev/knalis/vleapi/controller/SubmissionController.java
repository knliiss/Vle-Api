package dev.knalis.vleapi.controller;

import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import dev.knalis.vleapi.model.document.TestSubmissionDoc;
import dev.knalis.vleapi.model.dto.submission.FileSubmissionDto;
import dev.knalis.vleapi.model.dto.submission.TestSubmissionDto;
import dev.knalis.vleapi.service.intrf.CourseService;
import dev.knalis.vleapi.service.intrf.SubmissionService;
import dev.knalis.vleapi.service.intrf.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.Comparator;
import java.util.List;

import static dev.knalis.vleapi.security.Spel.*;

@Tag(name = "Submissions", description = "Operations related to file and test submissions and grades")
@RestController
@RequestMapping("/api/v1/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CourseService courseService;

    @Operation(summary = "Grade a file submission", security = @SecurityRequirement(name = "bearerAuth"), description = "Grade a previously submitted file. Only TEACHER or ADMINISTRATOR can grade. Teachers can grade only assigned courses.")
    @ApiResponse(responseCode = "200", description = "Submission graded",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileSubmissionDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid grade value", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Submission not found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @PreAuthorize(HAS_TEACHER + " or " + HAS_ADMIN)
    @PostMapping("/file/{submissionId}/grade")
    public ResponseEntity<FileSubmissionDto> gradeFileSubmission(@PathVariable String submissionId, @RequestParam Double grade) {
        if (grade == null || grade < 0) {
            return ResponseEntity.badRequest().body(null);
        }
        FileSubmissionDoc doc = submissionService.gradeFileSubmission(submissionId, grade);
        if (doc == null) {
            return ResponseEntity.status(404).body(null);
        }
        FileSubmissionDto dto = new FileSubmissionDto();
        dto.setId(doc.getId()); dto.setTaskId(doc.getTaskId()); dto.setUserId(doc.getUserId()); dto.setSubmitted(doc.getSubmitted()); dto.setStatus(doc.getStatus() == null ? null : doc.getStatus().name()); dto.setContentUrl(doc.getContentUrl()); dto.setGrade(doc.getGrade());
        return ResponseEntity.ok(dto);
    }
    
    @Operation(summary = "Grade a test submission", security = @SecurityRequirement(name = "bearerAuth"), description = "Grade a test submission (text). Only TEACHER or ADMINISTRATOR can grade.")
    @ApiResponse(responseCode = "200", description = "Submission graded",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestSubmissionDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid grade value", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Submission not found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @PreAuthorize(HAS_TEACHER + " or " + HAS_ADMIN)
    @PostMapping("/test/{submissionId}/grade")
    public ResponseEntity<TestSubmissionDto> gradeTestSubmission(@PathVariable String submissionId, @RequestParam Double grade) {
        if (grade == null || grade < 0) {
            return ResponseEntity.badRequest().body(null);
        }
        TestSubmissionDoc doc = submissionService.gradeTestSubmission(submissionId, grade);
        if (doc == null) {
            return ResponseEntity.status(404).body(null);
        }
        TestSubmissionDto dto = new TestSubmissionDto();
        dto.setId(doc.getId()); dto.setTaskId(doc.getTaskId()); dto.setUserId(doc.getUserId()); dto.setSubmitted(doc.getSubmitted()); dto.setContentUrl(doc.getContentUrl()); dto.setGrade(doc.getGrade());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get grade for a task for user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Returns grade or null")
    @PreAuthorize(HAS_ADMIN + " or " + HAS_TEACHER + " or " + IS_SELF_BY_USERID_PARAM)
    @GetMapping("/task/{taskId}/grade")
    public ResponseEntity<Double> getTaskGrade(@PathVariable Long taskId, @RequestParam Long userId) {
        Double grade = taskService.getGrade(taskId, userId);
        if (grade == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(grade);
    }

    @Operation(summary = "Get average grade for course for user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Returns average grade or null")
    @PreAuthorize(HAS_ADMIN + " or " + HAS_TEACHER + " or " + IS_SELF_BY_USERID_PARAM)
    @GetMapping("/course/{courseId}/grade")
    public ResponseEntity<Double> getCourseGrade(@PathVariable Long courseId, @RequestParam Long userId) {
        Double grade = courseService.getGradeForCourse(courseId, userId);
        if (grade == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(grade);
    }

    @Operation(summary = "Submit a file for a task", security = @SecurityRequirement(name = "bearerAuth"), description = "Multipart file upload. Student must be the userId and allowed to submit to the task.")
    @ApiResponse(responseCode = "200", description = "File submission created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileSubmissionDto.class)))
    @ApiResponse(responseCode = "400", description = "Bad request – missing parameters or file", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden – cannot submit for this task", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @PreAuthorize(HAS_STUDENT + " and " + IS_SELF_BY_USERID_PARAM + " and @accessControl.canSubmitTask(#taskId, principal.username)")
    @PostMapping(path = "/file/submit", consumes = {"multipart/form-data"})
    public ResponseEntity<FileSubmissionDto> submitFile(@RequestParam Long taskId, @RequestParam Long userId, @RequestPart("file") MultipartFile file) {
        if (taskId == null || userId == null) return ResponseEntity.badRequest().body(null);
        FileSubmissionDoc doc = submissionService.submitFile(taskId, userId, file);
        FileSubmissionDto dto = new FileSubmissionDto();
        dto.setId(doc.getId()); dto.setTaskId(doc.getTaskId()); dto.setUserId(doc.getUserId()); dto.setSubmitted(doc.getSubmitted()); dto.setStatus(doc.getStatus()==null?null:doc.getStatus().name()); dto.setContentUrl(doc.getContentUrl()); dto.setGrade(doc.getGrade());
        return ResponseEntity.ok(dto);
    }

    public static class TestSubmissionRequest { public Long taskId; public Long userId; public String content; }

    @Operation(summary = "Submit a test for a task", security = @SecurityRequirement(name = "bearerAuth"), description = "Submit textual/test answers as JSON string content.")
    @ApiResponse(responseCode = "200", description = "Test submission created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestSubmissionDto.class), examples = {@ExampleObject(value = "{\n  \"taskId\": 1,\n  \"userId\": 10,\n  \"content\": \"{\\\"answers\\\":[1,2,3]}\"\n}")}))
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @PreAuthorize(HAS_STUDENT + " and principal.username == @userService.findById(#req.userId).getUsername() and @accessControl.canSubmitTask(#req.taskId, principal.username)")
    @PostMapping(path = "/test/submit", consumes = {"application/json"})
    public ResponseEntity<TestSubmissionDto> submitTest(@org.springframework.web.bind.annotation.RequestBody TestSubmissionRequest req) {
        if (req == null || req.taskId == null || req.userId == null || req.content == null) {
            return ResponseEntity.badRequest().body(null);
        }
        TestSubmissionDoc doc = submissionService.submitTest(req.taskId, req.userId, req.content);
        TestSubmissionDto dto = new TestSubmissionDto();
        dto.setId(doc.getId()); dto.setTaskId(doc.getTaskId()); dto.setUserId(doc.getUserId()); dto.setSubmitted(doc.getSubmitted()); dto.setContentUrl(doc.getContentUrl()); dto.setContent(doc.getContent()); dto.setStatus(doc.getStatus()==null?null:doc.getStatus().name()); dto.setGrade(doc.getGrade());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "List my file submissions", security = @SecurityRequirement(name = "bearerAuth"), description = "List file submissions belonging to the authenticated student.")
    @ApiResponse(responseCode = "200", description = "List of file submissions")
    @PreAuthorize(HAS_ADMIN + " or " + HAS_TEACHER + " or (" + HAS_STUDENT + " and " + IS_SELF_BY_PATH_ID + ")")
    @GetMapping("/user/{id}/files")
    public ResponseEntity<List<FileSubmissionDto>> listMyFileSubmissions(@PathVariable Long id) {
        var docs = submissionService.findFileSubmissionsByUser(id);
        var dtos = docs.stream().map(d -> { FileSubmissionDto dto = new FileSubmissionDto(); dto.setId(d.getId()); dto.setTaskId(d.getTaskId()); dto.setUserId(d.getUserId()); dto.setSubmitted(d.getSubmitted()); dto.setStatus(d.getStatus()==null?null:d.getStatus().name()); dto.setContentUrl(d.getContentUrl()); dto.setGrade(d.getGrade()); return dto;}).toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "List my test submissions", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of test submissions")
    // Allow ADMIN or TEACHER to list any user's tests, or allow STUDENT to list their own
    @PreAuthorize(HAS_ADMIN + " or " + HAS_TEACHER + " or (" + HAS_STUDENT + " and " + IS_SELF_BY_PATH_ID + ")")
    @GetMapping("/user/{id}/tests")
    public ResponseEntity<List<TestSubmissionDto>> listMyTestSubmissions(@PathVariable Long id) {
        var docs = submissionService.findTestSubmissionsByUser(id);
        var dtos = docs.stream().map(d -> { TestSubmissionDto dto = new TestSubmissionDto(); dto.setId(d.getId()); dto.setTaskId(d.getTaskId()); dto.setUserId(d.getUserId()); dto.setSubmitted(d.getSubmitted()); dto.setContentUrl(d.getContentUrl()); dto.setContent(d.getContent()); dto.setGrade(d.getGrade()); return dto;}).toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Get latest file submission for task", security = @SecurityRequirement(name = "bearerAuth"), description = "Get the latest file submission for a task and user.")
    @ApiResponse(responseCode = "200", description = "Latest file submission for task")
    @ApiResponse(responseCode = "404", description = "No submissions found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @PreAuthorize(HAS_STUDENT + " and " + IS_SELF_BY_USERID_PARAM + " and @accessControl.canSubmitTask(#taskId, principal.username)")
    @GetMapping("/task/{taskId}/user/{userId}/file/latest")
    public ResponseEntity<FileSubmissionDto> latestFile(@PathVariable Long taskId, @PathVariable Long userId) {
        var list = submissionService.findFileSubmissionsByTaskAndUser(taskId, userId);
        var opt = list.stream().max(Comparator.comparing(FileSubmissionDoc::getSubmitted));
        if (opt.isEmpty()) return ResponseEntity.status(404).body(null);
        var d = opt.get(); FileSubmissionDto dto = new FileSubmissionDto(); dto.setId(d.getId()); dto.setTaskId(d.getTaskId()); dto.setUserId(d.getUserId()); dto.setSubmitted(d.getSubmitted()); dto.setStatus(d.getStatus()==null?null:d.getStatus().name()); dto.setContentUrl(d.getContentUrl()); dto.setGrade(d.getGrade());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get latest test submission for task", security = @SecurityRequirement(name = "bearerAuth"), description = "Get the latest test submission for a task and user.")
    @ApiResponse(responseCode = "200", description = "Latest test submission for task")
    @ApiResponse(responseCode = "404", description = "No submissions found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    @PreAuthorize(HAS_STUDENT + " and " + IS_SELF_BY_USERID_PARAM + " and @accessControl.canSubmitTask(#taskId, principal.username)")
    @GetMapping("/task/{taskId}/user/{userId}/test/latest")
    public ResponseEntity<TestSubmissionDto> latestTest(@PathVariable Long taskId, @PathVariable Long userId) {
        var list = submissionService.findTestSubmissionsByTaskAndUser(taskId, userId);
        var opt = list.stream().max(Comparator.comparing(TestSubmissionDoc::getSubmitted));
        if (opt.isEmpty()) return ResponseEntity.status(404).body(null);
        var d = opt.get(); TestSubmissionDto dto = new TestSubmissionDto(); dto.setId(d.getId()); dto.setTaskId(d.getTaskId()); dto.setUserId(d.getUserId()); dto.setSubmitted(d.getSubmitted()); dto.setContentUrl(d.getContentUrl()); dto.setContent(d.getContent()); dto.setGrade(d.getGrade());
        return ResponseEntity.ok(dto);
    }


}
