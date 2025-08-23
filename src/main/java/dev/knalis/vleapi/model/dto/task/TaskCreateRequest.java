package dev.knalis.vleapi.model.dto.task;

import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.task.TaskType;
import lombok.Data;

@Data
public class TaskCreateRequest {
    private String name;
    private String description;
    private double mark;
    private Course course;

    private TaskType type;
}
