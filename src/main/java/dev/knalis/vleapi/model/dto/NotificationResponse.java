package dev.knalis.vleapi.model.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class NotificationResponse {
    private Long id;
    private String subject;
    private String message;
    private Instant sentAt;
    private String status;
}

