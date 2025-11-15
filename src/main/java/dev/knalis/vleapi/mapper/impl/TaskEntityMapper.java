package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.task.TaskDto;
import dev.knalis.vleapi.model.entity.task.Task;
import dev.knalis.vleapi.model.entity.Topic;
import org.springframework.stereotype.Component;

@Component
public class TaskEntityMapper implements ObjectMapper<Task, TaskDto, TaskDto, TaskDto> {

    @Override
    public TaskDto toDto(Task entity) {
        if (entity == null) return null;
        TaskDto dto = new TaskDto(); dto.setId(entity.getId()); dto.setName(entity.getName()); dto.setDescription(entity.getDescription()); dto.setMaxMark(entity.getMaxMark()); dto.setCreationDate(entity.getCreationDate()); dto.setDueDate(entity.getDueDate()); dto.setTopicId(entity.getTopic()==null?null:entity.getTopic().getId()); return dto;
    }

    @Override
    public Task fromCreateRequest(TaskDto dto) { Task t = new Task(); t.setName(dto.getName()); t.setDescription(dto.getDescription()); t.setMaxMark(dto.getMaxMark()); t.setCreationDate(dto.getCreationDate()); t.setDueDate(dto.getDueDate()); if (dto.getTopicId()!=null){ Topic top=new Topic(); top.setId(dto.getTopicId()); t.setTopic(top);} return t; }

    @Override
    public void updateEntity(Task entity, TaskDto updateRequest) { if (updateRequest.getName()!=null) entity.setName(updateRequest.getName()); if (updateRequest.getDescription()!=null) entity.setDescription(updateRequest.getDescription()); if (updateRequest.getMaxMark()!=0) entity.setMaxMark(updateRequest.getMaxMark()); if (updateRequest.getDueDate()!=null) entity.setDueDate(updateRequest.getDueDate()); if (updateRequest.getTopicId()!=null){ Topic top=new Topic(); top.setId(updateRequest.getTopicId()); entity.setTopic(top);} }
}

