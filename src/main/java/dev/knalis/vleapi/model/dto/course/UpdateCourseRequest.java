package dev.knalis.vleapi.model.dto.course;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UpdateCourseRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;
}
