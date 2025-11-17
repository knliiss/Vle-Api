package dev.knalis.vleapi.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToMany
    @JoinTable(
            name = "group_course",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @JsonIgnore
    private List<Course> courses = new ArrayList<>();


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