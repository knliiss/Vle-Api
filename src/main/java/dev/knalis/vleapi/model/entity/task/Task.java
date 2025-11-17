package dev.knalis.vleapi.model.entity.task;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.knalis.vleapi.model.entity.Topic;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    
    private String taskType;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    @JsonBackReference
    private Topic topic;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "task-questions")
    private List<TestQuestion> questions = new ArrayList<>();

    public void addQuestion(TestQuestion q) {
        q.setTask(this);
        this.questions.add(q);
    }

    public void removeQuestion(TestQuestion q) {
        this.questions.remove(q);
        q.setTask(null);
    }
}
