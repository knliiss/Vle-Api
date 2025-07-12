package dev.knalis.vleapi.model.dto.topic;

import lombok.Data;

@Data
public class TopicDto {
    private Long id;

    private String title;

    private String content;

    private Long courseId;
}
