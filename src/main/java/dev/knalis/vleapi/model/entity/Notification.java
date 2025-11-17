package dev.knalis.vleapi.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long teacherId;
    private String subject;
    private String message;
    private Instant sentAt;
    private String status; // DELIVERED, READ, FAILED
}

