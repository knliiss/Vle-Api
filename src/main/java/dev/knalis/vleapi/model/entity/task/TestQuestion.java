package dev.knalis.vleapi.model.entity.task;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonBackReference(value = "task-questions")
    private Task task;

    @Column(name = "question_order", nullable = false)
    private Integer order;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "question_type", nullable = false)
    private String questionType;

    @Column(name = "options_json")
    private String optionsJson;

    private Double maxScore = 1.0;
}
