package dev.knalis.vleapi.model.entity;

import dev.knalis.vleapi.model.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "student_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "group_course",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses = new ArrayList<>();

    public void addUser(User user) {
        user.setGroup(this);
        this.users.add(user);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.setGroup(null);
    }

    public void addCourse(Course course) {
        if (!this.courses.contains(course)) {
            this.courses.add(course);
        }
        if (!course.getGroups().contains(this)) {
            course.getGroups().add(this);
        }
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.getGroups().remove(this);
    }

}