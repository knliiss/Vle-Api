package dev.knalis.vleapi.controller.version.v1.course;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.course.CourseDto;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.CourseService;
import dev.knalis.vleapi.mapper.impl.CourseEntityMapper;
import dev.knalis.vleapi.mapper.impl.TopicEntityMapper;
import dev.knalis.vleapi.model.dto.topic.TopicDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import dev.knalis.vleapi.model.entity.Topic;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.util.List;

import static dev.knalis.vleapi.security.Spel.*;

@Tag(name = "Courses", description = "Course management")
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController extends AbstractCRUDController<Course, CourseDto, CourseDto, CourseDto, Long> {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseEntityMapper courseMapper;

    @Autowired
    private TopicEntityMapper topicMapper;

    @Override
    protected CRUDService<Course, Long> getService() { return courseService; }

    @Override
    protected ObjectMapper<Course, CourseDto, CourseDto, CourseDto> getMapper() { return courseMapper; }

    @Override
    protected String getRestUrl() { return "courses"; }

    @Operation(summary = "Create a course", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize(HAS_ADMIN)
    @PostMapping
    @Override
    public ResponseEntity<CourseDto> create(@Valid @RequestBody CourseDto request) {
        return super.create(request);
    }

    @Operation(summary = "Update a course", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize(HAS_ADMIN)
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<CourseDto> update(@PathVariable Long id, @Valid @RequestBody CourseDto request) {
        return super.update(id, request);
    }

    @Operation(summary = "Delete a course", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize(HAS_ADMIN)
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }

    @Operation(summary = "Get course by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Course found")
    @PreAuthorize(HAS_ADMIN + " or (" + CAN_VIEW_COURSE + ")")
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> findById(@PathVariable Long id) {
        return super.findById(id);
    }

    @Operation(summary = "List topics for a course", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of topics",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TopicDto.class),
                    examples = @ExampleObject(value = "[{\n  \"id\": 5, \"name\": \"Intro\", \"description\": \"Basics\", \"courseId\": 1\n}]")))
    @PreAuthorize(CAN_VIEW_COURSE)
    @GetMapping("/{id}/topics")
    public ResponseEntity<?> listTopics(@PathVariable Long id) {
        Course course = courseService.findById(id); // 404 via handler if missing
        List<Topic> topics = course.getTopics();
        if (topics == null || topics.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        List<TopicDto> dtoList = topics.stream().map(topicMapper::toDto).toList();
        return ResponseEntity.ok(dtoList);
    }

}
