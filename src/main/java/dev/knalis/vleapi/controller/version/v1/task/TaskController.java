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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

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

    @Override
    protected CRUDService<Task, Long> getService() { return taskService; }

    @Override
    protected ObjectMapper<Task, TaskDto, TaskDto, TaskDto> getMapper() { return taskMapper; }

    @Override
    protected String getRestUrl() { return "tasks"; }

    @Operation(summary = "Create a task", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "Task created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class),
            examples = {@ExampleObject(value = "{\n  \"name\": \"Homework 1\",\n  \"description\": \"Do problems 1-10\",\n  \"maxMark\": 100,\n  \"dueDate\": \"2025-12-31T23:59:59Z\",\n  \"topicId\": 1\n}")}))
    @PreAuthorize(HAS_ADMIN + " or (" + CAN_CREATE_TASK + ")")
    @PostMapping
    @Override
    public ResponseEntity<TaskDto> create(@Valid @RequestBody TaskDto request) {
        return super.create(request);
    }

    @Operation(summary = "Get task by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Task found")
    @PreAuthorize(HAS_ADMIN + " or (" + CAN_VIEW_TASK + ")")
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> findById(@PathVariable Long id) {
        return super.findById(id);
    }

    @Operation(summary = "List all tasks", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of tasks")
    @PreAuthorize(HAS_ADMIN)
    @Override
    @GetMapping
    public ResponseEntity<List<TaskDto>> findAll() {
        return super.findAll();
    }

    @Operation(summary = "Update a task", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Task updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class),
            examples = {@ExampleObject(value = "{\n  \"name\": \"Homework 1 - updated\",\n  \"maxMark\": 120\n}")}))
    @PreAuthorize(HAS_ADMIN + " or (" + CAN_MANAGE_TASK + ")")
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<TaskDto> update(@PathVariable Long id, @Valid @RequestBody TaskDto request) {
        return super.update(id, request);
    }

    @Operation(summary = "Delete a task", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Task deleted")
    @PreAuthorize(HAS_ADMIN + " or (" + CAN_MANAGE_TASK + ")")
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }

}
