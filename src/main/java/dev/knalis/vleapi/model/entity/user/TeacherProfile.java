package dev.knalis.vleapi.model.entity.user;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TeacherProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String academicTitle;

    private String department;
    private String workPhone;
    private String scientificDegree;
}
