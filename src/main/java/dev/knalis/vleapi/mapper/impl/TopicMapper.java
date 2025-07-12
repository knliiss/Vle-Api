package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.topic.CreateTopicRequest;
import dev.knalis.vleapi.model.dto.topic.TopicDto;
import dev.knalis.vleapi.model.dto.topic.UpdateTopicRequest;
import dev.knalis.vleapi.model.entity.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper implements ObjectMapper<Topic, TopicDto, CreateTopicRequest, UpdateTopicRequest> {

    @Override
    public TopicDto toDto(Topic topic) {
        TopicDto dto = new TopicDto();
        dto.setId(topic.getId());
        dto.setTitle(topic.getTitle());
        dto.setContent(topic.getContent());
        if (topic.getCourse() != null) {
            dto.setCourseId(topic.getCourse().getId());
        }
        return dto;
    }

    @Override
    public Topic fromCreateRequest(CreateTopicRequest dto) {
        Topic topic = new Topic();
        topic.setTitle(dto.getTitle());
        topic.setContent(dto.getContent());
        return topic;
    }

    @Override
    public void updateEntity(Topic topic, UpdateTopicRequest updateTopicRequest) {
        if (updateTopicRequest.getTitle() != null) {
            topic.setTitle(updateTopicRequest.getTitle());
        }
        if (updateTopicRequest.getContent() != null) {
            topic.setContent(updateTopicRequest.getContent());
        }
    }
}
