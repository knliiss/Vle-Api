package dev.knalis.vleapi.model.document;

import dev.knalis.vleapi.model.entity.task.SubmissionStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "file_submissions")
public class FileSubmissionDoc {

    @Id
    private String id;

    @Indexed
    private Long taskId;

    @Indexed
    private Long userId;

    private LocalDateTime submitted;

    private SubmissionStatus status;

    private String contentUrl;

    private Double grade;
    
}
