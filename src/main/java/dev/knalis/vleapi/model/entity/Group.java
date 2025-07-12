package dev.knalis.vleapi.model.entity;

import dev.knalis.vleapi.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "student_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private short year;

    @ManyToMany
    @JoinTable(
            name = "group_course",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "group")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
        user.setGroup(this);
    }

    public void addCourse(Course course) {
        courses.add(course);
        course.getGroups().add(this);
    }

}