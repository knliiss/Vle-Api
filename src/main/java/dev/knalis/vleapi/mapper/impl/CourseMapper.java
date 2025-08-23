package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.course.CourseDto;
import dev.knalis.vleapi.model.dto.course.CourseCreateRequest;
import dev.knalis.vleapi.model.dto.course.CourseUpdateRequest;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.model.entity.Topic;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper implements ObjectMapper<Course, CourseDto, CourseCreateRequest, CourseUpdateRequest> {

    @Override
    public CourseDto toDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setTopicIds(course.getTopics().stream().map(Topic::getId).toList());
        dto.setGroupIds(course.getGroups().stream().map(Group::getId).toList());
        return dto;
    }

    @Override
    public Course fromCreateRequest(CourseCreateRequest dto) {
        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        return course;
    }

    @Override
    public void updateEntity(Course course, CourseUpdateRequest courseUpdateRequest) {
        if (courseUpdateRequest.getName() != null) {
            course.setName(courseUpdateRequest.getName());
        }
        if (courseUpdateRequest.getDescription() != null) {
            course.setDescription(courseUpdateRequest.getDescription());
        }
    }
}
