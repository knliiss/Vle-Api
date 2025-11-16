package dev.knalis.vleapi.controller.version.v1.topic;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.topic.TopicDto;
import dev.knalis.vleapi.model.entity.Topic;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.TopicService;
import dev.knalis.vleapi.mapper.impl.TopicEntityMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import dev.knalis.vleapi.model.entity.task.Task;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

import static dev.knalis.vleapi.security.Spel.*;

@Tag(name = "Topics", description = "Topic management")
@RestController
@RequestMapping("/api/v1/topics")
public class TopicController extends AbstractCRUDController<Topic, TopicDto, TopicDto, TopicDto, Long> {

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicEntityMapper topicMapper;

    @Override
    protected CRUDService<Topic, Long> getService() { return topicService; }

    @Override
    protected ObjectMapper<Topic, TopicDto, TopicDto, TopicDto> getMapper() { return topicMapper; }

    @Override
    protected String getRestUrl() { return "topics"; }

    @Operation(summary = "Create a topic", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "Topic created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TopicDto.class),
            examples = {@ExampleObject(value = "{\n  \"name\": \"Introduction\",\n  \"description\": \"Basics\",\n  \"courseId\": 1\n}")}))
    @PreAuthorize(HAS_ADMIN + " or (" + CAN_CREATE_TOPIC + ")")
    @PostMapping
    @Override
    public ResponseEntity<TopicDto> create(@Valid @RequestBody TopicDto request) {
        return super.create(request);
    }

    @Operation(summary = "Get topic by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Topic found")
    @PreAuthorize(HAS_ADMIN + " or (" + CAN_VIEW_TOPIC + ")")
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<TopicDto> findById(@PathVariable Long id) {
        return super.findById(id);
    }

    // List all topics: admin only (to avoid leaking other courses)
    @Operation(summary = "List all topics", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of topics")
    @PreAuthorize(HAS_ADMIN)
    @Override
    @GetMapping
    public ResponseEntity<List<TopicDto>> findAll() {
        return super.findAll();
    }

    // List tasks for topic — access like viewing topic
    @Operation(summary = "List tasks for a topic", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of tasks")
    @PreAuthorize(HAS_ADMIN + " or (" + CAN_VIEW_TOPIC + ")")
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<Task>> listTasks(@PathVariable Long id) {
        Topic topic = topicService.findById(id);
        if (topic == null) return ResponseEntity.status(404).body(null);
        return ResponseEntity.ok(topic.getTasks());
    }

    @Operation(summary = "Update a topic", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Topic updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TopicDto.class),
            examples = {@ExampleObject(value = "{\n  \"name\": \"Introduction - updated\"\n}")}))
    @PreAuthorize(HAS_ADMIN + " or (" + CAN_MANAGE_TOPIC + ")")
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<TopicDto> update(@PathVariable Long id, @Valid @RequestBody TopicDto request) {
        return super.update(id, request);
    }

    @Operation(summary = "Delete a topic", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Topic deleted")
    @PreAuthorize(HAS_ADMIN + " or (" + CAN_MANAGE_TOPIC + ")")
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }

}
