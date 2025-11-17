package dev.knalis.vleapi.controller.version.v1.topic;

import dev.knalis.vleapi.model.entity.Topic;
import dev.knalis.vleapi.service.intrf.TopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import dev.knalis.vleapi.model.dto.topic.TopicDto;
import dev.knalis.vleapi.model.dto.task.TaskDto;
import dev.knalis.vleapi.mapper.impl.TopicEntityMapper;
import dev.knalis.vleapi.mapper.impl.TaskEntityMapper;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import static dev.knalis.vleapi.security.Spel.CAN_VIEW_TOPIC;

@Tag(name = "Topics", description = "Topic management and relations")
@RestController
@RequestMapping("/api/v1/topics")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicEntityMapper topicMapper;

    @Autowired
    private TaskEntityMapper taskMapper;

    @Operation(summary = "Get topic by id", description = "Returns a topic by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Topic found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TopicDto.class)))
    @ApiResponse(responseCode = "404", description = "Topic not found", content = @Content(mediaType = "application/json"))
    @PreAuthorize(CAN_VIEW_TOPIC)
    @GetMapping("/{id}")
    public ResponseEntity<?> getTopic(@PathVariable Long id) {
        Topic topic = topicService.findById(id);
        if (topic == null) {
            return ResponseEntity.status(404).body(null);
        }
        TopicDto dto = topicMapper.toDto(topic);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get all tasks for a topic", description = "Returns all tasks linked to the topic", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of tasks", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskDto.class)))
    @PreAuthorize(CAN_VIEW_TOPIC)
    @GetMapping("/{id}/tasks")
    public ResponseEntity<?> listTasks(@PathVariable Long id) {
        Topic topic = topicService.findById(id);
        if (topic == null) {
            return ResponseEntity.status(404).body(null);
        }
        List<TaskDto> taskDtos = topic.getTasks() == null ? List.of() : topic.getTasks().stream().map(taskMapper::toDto).toList();
        return ResponseEntity.ok(taskDtos);
    }

    @Operation(summary = "Get all topics", description = "Returns all topics", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of topics", content = @Content(mediaType = "application/json"))
    @GetMapping
    public ResponseEntity<?> listTopics() {
        return ResponseEntity.ok(topicService.findAll());
    }
}
