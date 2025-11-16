package dev.knalis.vleapi.model.dto.submission;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
public class TestSubmissionDto {
    private String id;
    private Long taskId;
    private Long userId;
    private LocalDateTime submitted;
    private String contentUrl;
    private String content;
    @Schema(description = "Submission status", allowableValues = {"ADDED","OVERDUE","GRADED","RETURNED","REMOVED"})
    private String status;
    private Double grade;
    
}
