package dev.knalis.vleapi.model.entity;

import dev.knalis.vleapi.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonManagedReference(value = "course-topics")
    private List<Topic> topics = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    @JsonIgnore
    private List<Group> groups = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "course_teachers",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private List<User> teachers = new ArrayList<>();

    public void addTopic(Topic topic) {
        topic.setCourse(this);
        this.topics.add(topic);
    }

    public void removeTopic(Topic topic) {
        this.topics.remove(topic);
        topic.setCourse(null);
    }
    
    public void addTeacher(User teacher) {
        this.teachers.add(teacher);
    }

    public void removeTeacher(User teacher) {
        this.teachers.remove(teacher);
    }

}