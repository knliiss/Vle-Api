package dev.knalis.vleapi.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StudentProfileUpdateRequest {
    @Schema(example = "3", description = "Group id to assign/remove")
    private Long groupId; // null to unassign
}

