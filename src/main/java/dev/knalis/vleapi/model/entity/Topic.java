package dev.knalis.vleapi.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.knalis.vleapi.model.entity.task.Task;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference(value = "course-topics")
    private Course course;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Task> tasks = new ArrayList<>();

    public void addTask(Task task) {
        task.setTopic(this);
        this.tasks.add(task);
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
        task.setTopic(null);
    }

}