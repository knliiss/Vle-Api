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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping
    @Override
    public ResponseEntity<TaskDto> create(@Valid @RequestBody TaskDto request) {
        return super.create(request);
    }

}
