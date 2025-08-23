package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.task.TaskCreateRequest;
import dev.knalis.vleapi.model.dto.task.TaskDto;
import dev.knalis.vleapi.model.dto.task.TaskUpdateRequest;
import dev.knalis.vleapi.model.entity.task.Task;

public class TaskMapper implements ObjectMapper<Task, TaskDto, TaskCreateRequest, TaskUpdateRequest> {
    @Override
    public TaskDto toDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setCourse(task.getCourse());
        dto.setMark(task.getMark());
        dto.setType(task.getType());
        return dto;
    }

    @Override
    public Task fromCreateRequest(TaskCreateRequest dto) {
        Task task = new Task();
        task.setName(task.getName());
        task.setDescription(task.getDescription());
        task.setCourse(task.getCourse());
        task.setMark(task.getMark());
        task.setType(task.getType());
        return task;
    }

    @Override
    public void updateEntity(Task task, TaskUpdateRequest taskUpdateRequest) {
        if (taskUpdateRequest.getName() != null) {
            task.setName(taskUpdateRequest.getName());
        }
        if (taskUpdateRequest.getDescription() != null) {
            task.setDescription(taskUpdateRequest.getDescription());
        }

        if (taskUpdateRequest.getCourse() != null) {
            task.setCourse(taskUpdateRequest.getCourse());
        }

        if (taskUpdateRequest.getType() != null) {
            task.setType(taskUpdateRequest.getType());
        }

        if (taskUpdateRequest.getMark() != 0) {
            task.setMark(taskUpdateRequest.getMark());
        }

    }
}
