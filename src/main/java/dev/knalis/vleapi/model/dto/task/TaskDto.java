package dev.knalis.vleapi.model.dto.task;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
public class TaskDto {
    private Long id;
    
    @NotBlank
    @Schema(example = "Homework 1")
    private String name;
    private String description;
    
    @NotBlank
    @Schema(example = "TEST, LAB")
    private String taskType;
    private double maxMark;
    private Date creationDate;
    private Date dueDate;
    private Long topicId;

}
