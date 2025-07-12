package dev.knalis.vleapi.model.dto.course;

import lombok.Data;
import java.util.List;

@Data
public class CourseDto {
    private Long id;

    private String name;

    private List<Long> topicIds;

    private List<Long> groupIds;
}
