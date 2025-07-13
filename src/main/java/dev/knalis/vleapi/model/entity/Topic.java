package dev.knalis.vleapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ElementCollection
    @CollectionTable(name = "topic_files", joinColumns = @JoinColumn(name = "topic_id"))
    @Column(name = "file_url")
    private List<String> fileUrls = new ArrayList<>();
}