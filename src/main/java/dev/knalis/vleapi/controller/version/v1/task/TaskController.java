package dev.knalis.vleapi.controller.version.v1.task;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.impl.TaskEntityMapper;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.task.TaskDto;
import dev.knalis.vleapi.model.entity.task.Task;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.SubmissionService;
import dev.knalis.vleapi.service.intrf.TaskService;
import dev.knalis.vleapi.service.intrf.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

//    @Operation(summary = "List submissions for a task and user", description = "Returns both file and test submissions for the given task and user. If userId is omitted, the authenticated student's id is used. Teachers/Admins must specify userId explicitly.", security = @SecurityRequirement(name = "bearerAuth"))
//    @ApiResponse(responseCode = "200", description = "Lists of submissions", content = @Content(mediaType = "application/json"))
//    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/problem+json"))
//    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/problem+json"))
//    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/problem+json"))
//    @PreAuthorize("(" + HAS_STUDENT + " and @accessControl.canSubmitTask(#taskId, principal.username)) or ((" + HAS_TEACHER + " or " + HAS_ADMIN + ") and @accessControl.canViewTask(#taskId, principal.username))")
//    @GetMapping("/{taskId}/submissions")
//    public ResponseEntity<?> listTaskSubmissions(@PathVariable Long taskId) {
//        Task task = taskService.findById(taskId);
//        if (task == null) {
//            ProblemDetail pd = ProblemDetail.forStatus(404);
//            pd.setDetail("Task not found");
//            return ResponseEntity.status(404).body(pd);
//        }
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || !auth.isAuthenticated()) {
//            ProblemDetail pd = ProblemDetail.forStatus(401);
//            pd.setDetail("Not authenticated");
//            return ResponseEntity.status(401).body(pd);
//        }
//        boolean isTeacherOrAdmin = auth.getAuthorities().stream().anyMatch(a -> {
//            String r = a.getAuthority();
//            return "ROLE_TEACHER".equals(r) || "ROLE_ADMINISTRATOR".equals(r);
//        });
//        boolean isStudent = auth.getAuthorities().stream().anyMatch(a -> "ROLE_STUDENT".equals(a.getAuthority()));
//        if (task.getTaskType().equals("TEST")) {
//            List<TestSubmissionDto> submissionDtos = new ArrayList<>();
//            if (isTeacherOrAdmin) {
//
//            }
//        }
//    }

}
