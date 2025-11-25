package dev.knalis.vleapi.model.dto.submission;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Data
public class CombinedSubmissionDto {
    private String id;
    private Long taskId;
    private Long userId;
    private LocalDateTime submitted;
    private String status;
    private Double grade;

    private String fileName;
    private String contentUrl;
    private String mimeType;
    private Long size;

    private Object content;
}

