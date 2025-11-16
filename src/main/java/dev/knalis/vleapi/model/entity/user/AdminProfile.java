package dev.knalis.vleapi.model.entity.user;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class AdminProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String department;
}

