package dev.knalis.vleapi.controller.version.v1.impl;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.topic.CreateTopicRequest;
import dev.knalis.vleapi.model.dto.topic.TopicDto;
import dev.knalis.vleapi.model.dto.topic.UpdateTopicRequest;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.Topic;
import dev.knalis.vleapi.service.amazon.FileUploadService;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.util.ObjectBinder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(TopicController.TOPIC_REST_URL)
@RequiredArgsConstructor
public class TopicController extends AbstractCRUDController<Topic, TopicDto, CreateTopicRequest, UpdateTopicRequest, Long> {

    public static final String TOPIC_REST_URL = "/api/v1/topics";

    private final CRUDService<Topic, Long> topicService;
    private final CRUDService<Course, Long> courseService;

    private final ObjectMapper<Topic, TopicDto, CreateTopicRequest, UpdateTopicRequest> topicMapper;
    private final ObjectBinder objectBinder;

    private final FileUploadService uploadService;


    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Topic topic = getService().findById(id);
        objectBinder.unbindTopicFromCourse(id, topic.getCourse().getId());
        return super.delete(id);
    }

    @Override
    @PostMapping
    public ResponseEntity<TopicDto> create(@RequestBody CreateTopicRequest request) {
        Topic topic = getMapper().fromCreateRequest(request);
        topic = getService().create(topic);
        Course course = courseService.findById(request.getCourseId());
        objectBinder.bindTopicToCourse(topic.getId(), course.getId());

        return ResponseEntity.ok(getMapper().toDto(topic));
    }

    @Override
    protected CRUDService<Topic, Long> getService() {
        return topicService;
    }

    @Override
    protected ObjectMapper<Topic, TopicDto, CreateTopicRequest, UpdateTopicRequest> getMapper() {
        return topicMapper;
    }

    @Override
    protected String getRestUrl() {
        return TOPIC_REST_URL;
    }

    @PostMapping("/{id}/add-file")
    public ResponseEntity<TopicDto> uploadFileToTopic(@PathVariable Long id,
                                                      @RequestParam("file") MultipartFile file) {
        Topic topic = topicService.findById(id);
        String fileUrl = uploadService.uploadFile(file);

        topic.getFileUrls().add(fileUrl);
        topic = topicService.update(topic);
        return ResponseEntity.ok(getMapper().toDto(topic));
    }

    @DeleteMapping("/{id}/remove-file")
    public ResponseEntity<TopicDto> removeFileFromTopic(@PathVariable Long id,
                                                        @RequestParam("fileUrl") String fileUrl) {
        Topic topic = topicService.findById(id);

        if (!topic.getFileUrls().contains(fileUrl)) {
            return ResponseEntity.notFound().build();
        }

        uploadService.deleteFile(fileUrl);

        topic.getFileUrls().remove(fileUrl);
        topic = topicService.update(topic);

        return ResponseEntity.ok(getMapper().toDto(topic));
    }
}
