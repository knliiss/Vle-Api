package dev.knalis.vleapi.model.dto.topic;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTopicRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

}
