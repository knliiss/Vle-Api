package dev.knalis.vleapi.model.dto.topic;

import lombok.Data;

import java.util.List;

@Data
public class TopicDto {
    private Long id;
    private String title;
    private String content;
    private Long courseId;
    private List<String> fileUrls;
}
