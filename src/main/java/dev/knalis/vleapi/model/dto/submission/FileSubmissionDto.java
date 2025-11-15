package dev.knalis.vleapi.model.dto.submission;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileSubmissionDto {
    private String id;
    private Long taskId;
    private Long userId;
    private LocalDateTime submitted;
    private String status;
    private String contentUrl;
    private Double grade;
    
}

