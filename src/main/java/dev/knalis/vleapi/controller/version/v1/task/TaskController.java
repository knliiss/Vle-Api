package dev.knalis.vleapi.controller.version.v1.task;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.task.TaskDto;
import dev.knalis.vleapi.model.entity.task.Task;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.TaskService;
import dev.knalis.vleapi.mapper.impl.TaskEntityMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import dev.knalis.vleapi.model.dto.submission.FileSubmissionDto;
import dev.knalis.vleapi.model.dto.submission.TestSubmissionDto;
import dev.knalis.vleapi.service.intrf.SubmissionService;
import dev.knalis.vleapi.service.intrf.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ProblemDetail;

import java.util.Map;
import java.util.List;

import static dev.knalis.vleapi.security.Spel.*;

@Tag(name = "Tasks", description = "Task management")
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController extends AbstractCRUDController<Task, TaskDto, TaskDto, TaskDto, Long> {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskEntityMapper taskMapper;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    @Override
    protected CRUDService<Task, Long> getService() {
        return taskService;
    }

    @Override
    protected ObjectMapper<Task, TaskDto, TaskDto, TaskDto> getMapper() {
        return taskMapper;
    }

    @Override
    protected String getRestUrl() {
        return "tasks";
    }

    @Operation(summary = "List tasks for a topic", description = "Returns tasks for the given topic", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of tasks", content = @Content(mediaType = "application/json"))
    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<List<TaskDto>> listByTopic(@PathVariable Long topicId) {
        List<Task> tasks = taskService.findAll().stream().filter(t -> t.getTopic() != null && topicId.equals(t.getTopic().getId())).toList();
        List<TaskDto> dtos = tasks.stream().map(taskMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "List submissions for a task and user", description = "Returns both file and test submissions for the given task and user. If userId is omitted, the authenticated student's id is used. Teachers/Admins must specify userId explicitly.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Lists of submissions", content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/problem+json"))
    @PreAuthorize("(" + HAS_STUDENT + " and @accessControl.canSubmitTask(#taskId, principal.username)) or ((" + HAS_TEACHER + " or " + HAS_ADMIN + ") and @accessControl.canViewTask(#taskId, principal.username))")
    @GetMapping("/{taskId}/submissions")
    public ResponseEntity<?> listTaskSubmissions(
            @PathVariable Long taskId,
            @Parameter(description = "Optional user id; if omitted uses authenticated student user id, but is REQUIRED for teacher/admin")
            @RequestParam(name = "userId", required = false) Long userId) {
        Task task = taskService.findById(taskId);
        if (task == null) {
            ProblemDetail pd = ProblemDetail.forStatus(404);
            pd.setDetail("Task not found");
            return ResponseEntity.status(404).body(pd);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            ProblemDetail pd = ProblemDetail.forStatus(401);
            pd.setDetail("Not authenticated");
            return ResponseEntity.status(401).body(pd);
        }
        boolean isTeacherOrAdmin = auth.getAuthorities().stream().anyMatch(a -> {
            String r = a.getAuthority();
            return "ROLE_TEACHER".equals(r) || "ROLE_ADMINISTRATOR".equals(r);
        });
        boolean isStudent = auth.getAuthorities().stream().anyMatch(a -> "ROLE_STUDENT".equals(a.getAuthority()));
        if (userId == null) {
            if (isTeacherOrAdmin) {
                ProblemDetail pd = ProblemDetail.forStatus(400);
                pd.setDetail("userId parameter is required for teacher/admin");
                return ResponseEntity.badRequest().body(pd);
            }
            if (isStudent) {
                var user = userService.findByUsername(auth.getName());
                if (user == null) {
                    ProblemDetail pd = ProblemDetail.forStatus(400);
                    pd.setDetail("Unable to resolve current user");
                    return ResponseEntity.badRequest().body(pd);
                }
                userId = user.getId();
            }
        }
        if (userId == null) {
            ProblemDetail pd = ProblemDetail.forStatus(400);
            pd.setDetail("userId could not be resolved");
            return ResponseEntity.badRequest().body(pd);
        }
        var fileDocs = submissionService.findFileSubmissionsByTaskAndUser(taskId, userId);
        var testDocs = submissionService.findTestSubmissionsByTaskAndUser(taskId, userId);
        var fileDtos = fileDocs.stream().map(d -> { FileSubmissionDto dto = new FileSubmissionDto(); dto.setId(d.getId()); dto.setTaskId(d.getTaskId()); dto.setUserId(d.getUserId()); dto.setSubmitted(d.getSubmitted()); dto.setStatus(d.getStatus()==null?null:d.getStatus().name()); dto.setContentUrl(d.getContentUrl()); dto.setGrade(d.getGrade()); return dto;}).toList();
        var testDtos = testDocs.stream().map(d -> { TestSubmissionDto dto = new TestSubmissionDto(); dto.setId(d.getId()); dto.setTaskId(d.getTaskId()); dto.setUserId(d.getUserId()); dto.setSubmitted(d.getSubmitted()); dto.setContentUrl(d.getContentUrl()); dto.setContent(d.getContent()); dto.setStatus(d.getStatus()==null?null:d.getStatus().name()); dto.setGrade(d.getGrade()); return dto;}).toList();
        return ResponseEntity.ok(Map.of("files", fileDtos, "tests", testDtos));
    }

}
