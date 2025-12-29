package dev.knalis.vleapi.controller.version.v1.task;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.impl.TaskEntityMapper;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.task.TaskDto;
import dev.knalis.vleapi.model.entity.task.Task;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.TaskService;
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

}
