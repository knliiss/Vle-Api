package dev.knalis.vleapi.mapper.impl;

import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.course.CourseDto;
import dev.knalis.vleapi.model.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseEntityMapper implements ObjectMapper<Course, CourseDto, CourseDto, CourseDto> {
    @Override
    public CourseDto toDto(Course entity) {
        if (entity == null) return null;
        CourseDto dto = new CourseDto(); dto.setId(entity.getId()); dto.setName(entity.getName()); return dto;
    }

    @Override
    public Course fromCreateRequest(CourseDto dto) { Course c = new Course(); c.setName(dto.getName()); return c; }

    @Override
    public void updateEntity(Course entity, CourseDto updateRequest) { if (updateRequest.getName()!=null) entity.setName(updateRequest.getName()); }
}
