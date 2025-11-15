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

    @Operation(summary = "Grade a file submission", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Submission graded",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileSubmissionDto.class)))
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMINISTRATOR')")
    @PostMapping("/file/{submissionId}/grade")
    public ResponseEntity<FileSubmissionDto> gradeFileSubmission(@PathVariable String submissionId, @RequestParam Double grade) {
        FileSubmissionDoc doc = submissionService.gradeFileSubmission(submissionId, grade);
        FileSubmissionDto dto = new FileSubmissionDto();
        dto.setId(doc.getId()); dto.setTaskId(doc.getTaskId()); dto.setUserId(doc.getUserId()); dto.setSubmitted(doc.getSubmitted()); dto.setStatus(doc.getStatus() == null ? null : doc.getStatus().name()); dto.setContentUrl(doc.getContentUrl()); dto.setGrade(doc.getGrade());
        return ResponseEntity.ok(dto);
    }
    
    @Operation(summary = "Grade a test submission", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Submission graded",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TestSubmissionDto.class)))
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMINISTRATOR')")
    @PostMapping("/test/{submissionId}/grade")
    public ResponseEntity<TestSubmissionDto> gradeTestSubmission(@PathVariable String submissionId, @RequestParam Double grade) {
        TestSubmissionDoc doc = submissionService.gradeTestSubmission(submissionId, grade);
        TestSubmissionDto dto = new TestSubmissionDto();
        dto.setId(doc.getId()); dto.setTaskId(doc.getTaskId()); dto.setUserId(doc.getUserId()); dto.setSubmitted(doc.getSubmitted()); dto.setContentUrl(doc.getContentUrl()); dto.setGrade(doc.getGrade());
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get grade for a task for user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Returns grade or null")
    @GetMapping("/task/{taskId}/grade")
    public ResponseEntity<Double> getTaskGrade(@PathVariable Long taskId, @RequestParam Long userId) {
        Double grade = taskService.getGrade(taskId, userId);
        return ResponseEntity.ok(grade);
    }

    @Operation(summary = "Get average grade for course for user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Returns average grade or null")
    @GetMapping("/course/{courseId}/grade")
    public ResponseEntity<Double> getCourseGrade(@PathVariable Long courseId, @RequestParam Long userId) {
        Double grade = courseService.getGradeForCourse(courseId, userId);
        return ResponseEntity.ok(grade);
    }

}
