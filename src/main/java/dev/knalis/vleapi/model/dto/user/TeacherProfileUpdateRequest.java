package dev.knalis.vleapi.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TeacherProfileUpdateRequest {
    @Schema(example = "PhD", description = "Academic title")
    private String academicTitle;
}

