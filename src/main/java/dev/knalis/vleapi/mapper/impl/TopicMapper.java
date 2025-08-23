package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.topic.TopicCreateRequest;
import dev.knalis.vleapi.model.dto.topic.TopicDto;
import dev.knalis.vleapi.model.dto.topic.TopicUpdateRequest;
import dev.knalis.vleapi.model.entity.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper implements ObjectMapper<Topic, TopicDto, TopicCreateRequest, TopicUpdateRequest> {

    @Override
    public TopicDto toDto(Topic topic) {
        TopicDto dto = new TopicDto();
        dto.setId(topic.getId());
        dto.setTitle(topic.getTitle());
        dto.setContent(topic.getContent());
        if (topic.getCourse() != null) {
            dto.setCourseId(topic.getCourse().getId());
        }
        dto.setFileUrls(topic.getFileUrls());
        return dto;
    }

    @Override
    public Topic fromCreateRequest(TopicCreateRequest dto) {
        Topic topic = new Topic();
        topic.setTitle(dto.getTitle());
        topic.setContent(dto.getContent());
        return topic;
    }

    @Override
    public void updateEntity(Topic topic, TopicUpdateRequest topicUpdateRequest) {
        if (topicUpdateRequest.getTitle() != null) {
            topic.setTitle(topicUpdateRequest.getTitle());
        }
        if (topicUpdateRequest.getContent() != null) {
            topic.setContent(topicUpdateRequest.getContent());
        }
    }
}
