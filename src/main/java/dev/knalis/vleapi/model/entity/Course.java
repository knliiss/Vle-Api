package dev.knalis.vleapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Topic> topics = new ArrayList<>();

    @ManyToMany(mappedBy = "courses", cascade = CascadeType.ALL)
    private List<Group> groups = new ArrayList<>();

    public void addTopic(Topic topic) {
        topics.add(topic);
        topic.setCourse(this);
    }

}