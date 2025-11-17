package dev.knalis.vleapi.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TeacherProfileUpdateRequest {
    @Schema(example = "PhD", description = "Academic title")
    private String academicTitle;
    @Schema(example = "Math Department", description = "Department name")
    private String department;
    @Schema(example = "+380123456789", description = "Work phone number")
    private String workPhone;
    @Schema(example = "Doctor of Science", description = "Scientific degree")
    private String scientificDegree;
}
