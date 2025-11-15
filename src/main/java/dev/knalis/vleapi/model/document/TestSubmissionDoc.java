package dev.knalis.vleapi.model.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "test_submissions")
public class TestSubmissionDoc {

    @Id
    private String id;

    private Long taskId;

    private Long userId;

    private LocalDateTime submitted;

    private String contentUrl;

    private Double grade;
    
}

