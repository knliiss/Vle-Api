package dev.knalis.vleapi.controller.version.v1.task;

import dev.knalis.vleapi.mapper.impl.TestQuestionMapper;
import dev.knalis.vleapi.model.dto.task.TestQuestionDto;
import dev.knalis.vleapi.model.entity.task.Task;
import dev.knalis.vleapi.model.entity.task.TestQuestion;
import dev.knalis.vleapi.service.intrf.TaskService;
import dev.knalis.vleapi.service.intrf.TestQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import static dev.knalis.vleapi.security.Spel.HAS_ADMIN;
import static dev.knalis.vleapi.security.Spel.HAS_TEACHER;
import static dev.knalis.vleapi.security.Spel.HAS_STUDENT;

@Tag(name = "Test Questions", description = "Manage test task questions")
@RestController
@RequestMapping("/api/v1/test-questions")
public class TestQuestionController {

    @Autowired private TestQuestionService service;
    @Autowired private TestQuestionMapper mapper;
    @Autowired private TaskService taskService;

    @Operation(summary = "Create question", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize(HAS_ADMIN + " or " + HAS_TEACHER)
    @PostMapping
    public ResponseEntity<TestQuestionDto> create(@Valid @RequestBody TestQuestionDto dto) {
        Task task = taskService.findById(dto.getTaskId());
        if (task == null) {
            return ResponseEntity.status(404).body(null);
        }
        if (!"TEST".equalsIgnoreCase(task.getTaskType())) {
            ProblemDetail pd = ProblemDetail.forStatus(400);
            pd.setDetail("Task type must be TEST to attach questions");
            return ResponseEntity.badRequest().body(null);
        }
        boolean orderExists = service.listByTask(dto.getTaskId()).stream().anyMatch(q -> q.getOrder().equals(dto.getOrder()));
        if (orderExists) {
            ProblemDetail pd = ProblemDetail.forStatus(409);
            pd.setDetail("Question order already exists for this task");
            return ResponseEntity.status(409).body(null);
        }
        if (!"FREE_TEXT".equals(dto.getQuestionType())) {
            if (dto.getOptionsJson() == null || dto.getOptionsJson().isBlank()) {
                ProblemDetail pd = ProblemDetail.forStatus(400);
                pd.setDetail("optionsJson required for choice question types");
                return ResponseEntity.badRequest().body(null);
            }
            if (!dto.getOptionsJson().trim().startsWith("[") || !dto.getOptionsJson().trim().endsWith("]")) {
                ProblemDetail pd = ProblemDetail.forStatus(400);
                pd.setDetail("optionsJson must be a JSON array");
                return ResponseEntity.badRequest().body(null);
            }
        }
        TestQuestion q = mapper.fromCreate(dto);
        return ResponseEntity.ok(mapper.toDto(service.create(q)));
    }

    @Operation(summary = "Update question", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize(HAS_ADMIN + " or " + HAS_TEACHER)
    @PatchMapping("/{id}")
    public ResponseEntity<TestQuestionDto> patch(@PathVariable Long id, @Valid @RequestBody TestQuestionDto dto) {
        TestQuestion existing = service.findById(id);
        if (dto.getOrder() != null && !dto.getOrder().equals(existing.getOrder())) {
            boolean orderExists = service.listByTask(existing.getTask().getId()).stream().anyMatch(q -> q.getOrder().equals(dto.getOrder()));
            if (orderExists) {
                return ResponseEntity.status(409).body(null);
            }
        }
        if (dto.getQuestionType() != null && !"FREE_TEXT".equals(dto.getQuestionType())) {
            if (dto.getOptionsJson() != null) {
                if (!dto.getOptionsJson().trim().startsWith("[") || !dto.getOptionsJson().trim().endsWith("]")) {
                    return ResponseEntity.badRequest().body(null);
                }
            }
        }
        mapper.update(existing, dto);
        return ResponseEntity.ok(mapper.toDto(service.update(existing)));
    }

    @Operation(summary = "Delete question", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize(HAS_ADMIN + " or " + HAS_TEACHER)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List questions by task", security = @SecurityRequirement(name = "bearerAuth"), description = "Admins/Teachers can view. Students can view if they have access to the task via course/group policies.")
    @PreAuthorize(HAS_ADMIN + " or " + HAS_TEACHER + " or (" + HAS_STUDENT + " and @accessControl.canViewTask(#taskId, principal.username))")
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TestQuestionDto>> listByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(service.listByTask(taskId).stream().map(mapper::toDto).toList());
    }
}
