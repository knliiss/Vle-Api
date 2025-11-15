package dev.knalis.vleapi.model.dto.course;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class CourseDto {
    private Long id;
    @NotBlank
    @Schema(example = "Algorithms 101")
    private String name;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
}
