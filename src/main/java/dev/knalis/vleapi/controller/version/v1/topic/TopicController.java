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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping
    @Override
    public ResponseEntity<TopicDto> create(@Valid @RequestBody TopicDto request) {
        return super.create(request);
    }

}
