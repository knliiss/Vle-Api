package dev.knalis.vleapi.model.dto.group;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class GroupDto {
    private Long id;
    @NotBlank
    @Schema(example = "Group A")
    private String name;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
}
