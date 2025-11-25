package dev.knalis.vleapi.model.dto.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupDto {
    private Long id;
    
    @NotBlank
    @Schema(example = "Group A")
    private String name;

    @Schema(example = "2025")
    private Integer year;
    
}
