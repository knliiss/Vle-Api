package dev.knalis.vleapi.model.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateGroupRequest {

    @NotBlank
    private String name;

    @Size(min = 1, max = 6, message = "Group year must be between 1 and 6 characters long")
    private short year;

}
