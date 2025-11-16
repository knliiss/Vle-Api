package dev.knalis.vleapi.model.dto.submission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TestSubmitRequest {
    @Schema(example = "{\"answers\": {...}}", description = "Test content as JSON/string")
    private String content;
}

