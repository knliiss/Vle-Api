package dev.knalis.vleapi.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdminProfileUpdateRequest {
    @Schema(example = "Education Department", description = "Department name")
    private String department;
}

