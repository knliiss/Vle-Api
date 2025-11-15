package dev.knalis.vleapi.model.dto.task;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskDto {
    private Long id;
    @NotBlank
    @Schema(example = "Homework 1")
    private String name;
    private String description;
    private double maxMark;
    private Date creationDate;
    private Date dueDate;
    private Long topicId;

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
    public double getMaxMark(){return maxMark;} public void setMaxMark(double maxMark){this.maxMark=maxMark;}
    public Date getCreationDate(){return creationDate;} public void setCreationDate(Date creationDate){this.creationDate=creationDate;}
    public Date getDueDate(){return dueDate;} public void setDueDate(Date dueDate){this.dueDate=dueDate;}
    public Long getTopicId(){return topicId;} public void setTopicId(Long topicId){this.topicId=topicId;}
}
