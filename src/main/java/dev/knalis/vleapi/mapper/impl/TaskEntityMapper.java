package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.task.TaskDto;
import dev.knalis.vleapi.model.entity.task.Task;
import dev.knalis.vleapi.model.entity.Topic;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class TaskEntityMapper implements ObjectMapper<Task, TaskDto, TaskDto, TaskDto> {
    
    @Override
    public TaskDto toDto(Task entity) {
        if (entity == null) return null;
        TaskDto dto = new TaskDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setMaxMark(entity.getMaxMark());
        dto.setCreationDate(entity.getCreationDate());
        dto.setDueDate(entity.getDueDate());
        dto.setTaskType(entity.getTaskType());
        dto.setTopicId(entity.getTopic() == null ? null : entity.getTopic().getId());
        return dto;
    }
    
    @Override
    public Task fromCreateRequest(TaskDto dto) {
        Task t = new Task();
        t.setName(dto.getName());
        t.setDescription(dto.getDescription());
        t.setMaxMark(dto.getMaxMark());
        t.setCreationDate(dto.getCreationDate());
        t.setDueDate(dto.getDueDate());
        t.setTaskType(dto.getTaskType());
        if (dto.getTopicId() != null) {
            Topic top = new Topic();
            top.setId(dto.getTopicId());
            t.setTopic(top);
        }
        
        if (dto.getCreationDate() == null) {
            t.setCreationDate(Date.from(Instant.now()));
        }
        
        if (dto.getDueDate() == null) {
            t.setDueDate(Date.from(Instant.now().plusSeconds(7 * 24 * 60 * 60)));
        }
        
        return t;
    }
    
    @Override
    public void updateEntity(Task entity, TaskDto updateRequest) {
        if (updateRequest.getName() != null) entity.setName(updateRequest.getName());
        if (updateRequest.getDescription() != null) entity.setDescription(updateRequest.getDescription());
        if (updateRequest.getMaxMark() != 0) entity.setMaxMark(updateRequest.getMaxMark());
        if (updateRequest.getDueDate() != null) entity.setDueDate(updateRequest.getDueDate());
        if (updateRequest.getTaskType() != null) entity.setTaskType(updateRequest.getTaskType());
        if (updateRequest.getTopicId() != null) {
            Topic top = new Topic();
            top.setId(updateRequest.getTopicId());
            entity.setTopic(top);
        }
    }
}

