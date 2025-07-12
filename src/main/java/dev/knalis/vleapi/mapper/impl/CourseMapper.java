package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.course.CourseDto;
import dev.knalis.vleapi.model.dto.course.CreateCourseRequest;
import dev.knalis.vleapi.model.dto.course.UpdateCourseRequest;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.model.entity.Topic;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper implements ObjectMapper<Course, CourseDto, CreateCourseRequest, UpdateCourseRequest> {

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
    public Course fromCreateRequest(CreateCourseRequest dto) {
        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        return course;
    }

    @Override
    public void updateEntity(Course course, UpdateCourseRequest updateCourseRequest) {
        if (updateCourseRequest.getName() != null) {
            course.setName(updateCourseRequest.getName());
        }
        if (updateCourseRequest.getDescription() != null) {
            course.setDescription(updateCourseRequest.getDescription());
        }
    }
}
