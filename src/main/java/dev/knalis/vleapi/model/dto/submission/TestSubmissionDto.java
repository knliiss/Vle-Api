package dev.knalis.vleapi.model.dto.submission;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestSubmissionDto {
    private String id;
    private Long taskId;
    private Long userId;
    private LocalDateTime submitted;
    private String contentUrl;
    private Double grade;
    
}

