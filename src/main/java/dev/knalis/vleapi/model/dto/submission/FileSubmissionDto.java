package dev.knalis.vleapi.model.dto.submission;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
public class FileSubmissionDto {
    private String id;
    private Long taskId;
    private Long userId;
    private LocalDateTime submitted;
    @Schema(description = "Submission status", allowableValues = {"ADDED","OVERDUE","GRADED","RETURNED","REMOVED"})
    private String status;
    private String contentUrl;
    private Double grade;
    
}
