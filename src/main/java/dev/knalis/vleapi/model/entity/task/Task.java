package dev.knalis.vleapi.model.entity.task;

import dev.knalis.vleapi.model.entity.Course;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    private double mark;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Course course;

    private TaskType type;
}
