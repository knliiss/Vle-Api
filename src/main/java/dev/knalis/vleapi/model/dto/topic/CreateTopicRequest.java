package dev.knalis.vleapi.model.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTopicRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private Long courseId;
}
