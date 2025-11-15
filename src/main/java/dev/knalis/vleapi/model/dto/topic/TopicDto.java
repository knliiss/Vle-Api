package dev.knalis.vleapi.model.dto.topic;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class TopicDto {
    private Long id;
    @NotBlank
    @Schema(example = "Introduction to Java")
    private String name;
    private String description;
    private Long courseId;

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
    public Long getCourseId(){return courseId;} public void setCourseId(Long courseId){this.courseId=courseId;}
}
