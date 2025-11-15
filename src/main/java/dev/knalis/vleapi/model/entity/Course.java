package dev.knalis.vleapi.model.entity;

import dev.knalis.vleapi.model.entity.task.Task;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Topic> topics = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    private List<Group> groups = new ArrayList<>();

    public void addTopic(Topic topic) {
        topic.setCourse(this);
        this.topics.add(topic);
    }

    public void removeTopic(Topic topic) {
        this.topics.remove(topic);
        topic.setCourse(null);
    }
    

}