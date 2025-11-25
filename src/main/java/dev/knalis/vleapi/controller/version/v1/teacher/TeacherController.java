package dev.knalis.vleapi.controller.version.v1.teacher;

import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.intrf.UserService;
import dev.knalis.vleapi.service.intrf.SubmissionService;
import dev.knalis.vleapi.model.dto.submission.CombinedSubmissionDto;
import dev.knalis.vleapi.model.document.FileSubmissionDoc;
import dev.knalis.vleapi.model.document.TestSubmissionDoc;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Tag(name = "Teachers", description = "Teacher management and relations")
@RestController
@RequestMapping("/api/v1/teachers")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private SubmissionService submissionService;

    @Operation(summary = "Get all courses for a teacher", description = "Returns all courses linked to the teacher", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of courses", content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> listCourses(@PathVariable Long id) {
        User teacher = userService.findById(id);
        return ResponseEntity.ok(userService.findCoursesForTeacher(id));
    }

    @Operation(summary = "Get teacher details", description = "Returns teacher with linked courses", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Teacher details", content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}")
    public ResponseEntity<?> getTeacherDetails(@PathVariable Long id) {
        User teacher = userService.findById(id);
        return ResponseEntity.ok(teacher);
    }

    @Operation(summary = "Get recent submissions for teacher", description = "Returns recent submissions across teacher's courses", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of recent submissions", content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}/submissions/recent")
    public ResponseEntity<List<CombinedSubmissionDto>> recentSubmissions(@PathVariable Long id, @RequestParam(name = "limit", required = false, defaultValue = "50") int limit) {
        var courses = userService.findCoursesForTeacher(id);
        var courseIds = courses.stream().map(c -> c.getId()).toList();

        List<CombinedSubmissionDto> merged = new ArrayList<>();

        for (Long courseId : courseIds) {
            List<FileSubmissionDoc> fileDocs = submissionService.findFileSubmissionsByCourseId(courseId);
            List<TestSubmissionDoc> testDocs = submissionService.findTestSubmissionsByCourseId(courseId);
            fileDocs.forEach(d -> {
                CombinedSubmissionDto dto = new CombinedSubmissionDto();
                dto.setId(d.getId()); dto.setTaskId(d.getTaskId()); dto.setUserId(d.getUserId()); dto.setSubmitted(d.getSubmitted()); dto.setStatus(d.getStatus()==null?null:d.getStatus().name()); dto.setGrade(d.getGrade()); dto.setContentUrl(d.getContentUrl()); dto.setFileName(null); dto.setMimeType(null); dto.setSize(null);
                merged.add(dto);
            });
            testDocs.forEach(d -> {
                CombinedSubmissionDto dto = new CombinedSubmissionDto();
                dto.setId(d.getId()); dto.setTaskId(d.getTaskId()); dto.setUserId(d.getUserId()); dto.setSubmitted(d.getSubmitted()); dto.setStatus(d.getStatus()==null?null:d.getStatus().name()); dto.setGrade(d.getGrade()); dto.setContent(d.getContent()); merged.add(dto);
            });
        }

        List<CombinedSubmissionDto> sorted = merged.stream().sorted(Comparator.comparing(CombinedSubmissionDto::getSubmitted).reversed()).limit(limit).toList();
        return ResponseEntity.ok(sorted);
    }
}
