package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.topic.TopicDto;
import dev.knalis.vleapi.model.entity.Topic;
import dev.knalis.vleapi.model.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class TopicEntityMapper implements ObjectMapper<Topic, TopicDto, TopicDto, TopicDto> {

    @Override
    public TopicDto toDto(Topic entity) {
        if (entity == null) return null;
        TopicDto dto = new TopicDto(); dto.setId(entity.getId()); dto.setName(entity.getName()); dto.setDescription(entity.getDescription()); dto.setCourseId(entity.getCourse()==null?null:entity.getCourse().getId()); return dto;
    }

    @Override
    public Topic fromCreateRequest(TopicDto dto) { Topic t = new Topic(); t.setName(dto.getName()); t.setDescription(dto.getDescription()); if (dto.getCourseId()!=null){ Course c=new Course(); c.setId(dto.getCourseId()); t.setCourse(c);} return t; }

    @Override
    public void updateEntity(Topic entity, TopicDto updateRequest) { if (updateRequest.getName()!=null) entity.setName(updateRequest.getName()); if (updateRequest.getDescription()!=null) entity.setDescription(updateRequest.getDescription()); if (updateRequest.getCourseId()!=null){ Course c=new Course(); c.setId(updateRequest.getCourseId()); entity.setCourse(c);} }
}

