package dev.knalis.vleapi.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Year;
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

    @Column(name = "year", nullable = false)
    private Integer year;

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

    @PrePersist
    public void ensureYear() {
        if (this.year == null) {
            this.year = Year.now().getValue();
        }
    }

}