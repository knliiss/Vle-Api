package dev.knalis.vleapi.model.entity.task;

import dev.knalis.vleapi.model.entity.Topic;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private double maxMark;

    private Date creationDate;

    private Date dueDate;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;


}
