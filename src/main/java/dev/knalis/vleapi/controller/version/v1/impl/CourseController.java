package dev.knalis.vleapi.controller.version.v1.impl;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.impl.CourseMapper;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.course.CourseDto;
import dev.knalis.vleapi.model.dto.course.CreateCourseRequest;
import dev.knalis.vleapi.model.dto.course.UpdateCourseRequest;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.util.ObjectBinder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(CourseController.COURSE_REST_URL)
@RequiredArgsConstructor
public class CourseController extends AbstractCRUDController<Course, CourseDto, CreateCourseRequest, UpdateCourseRequest, Long> {

    public static final String COURSE_REST_URL = "/api/v1/courses";

    private final CRUDService<Course, Long> courseService;
    private final CourseMapper courseMapper;
    private final ObjectBinder objectBinder;

    @Override
    protected CRUDService<Course, Long> getService() {
        return courseService;
    }

    @Override
    protected ObjectMapper<Course, CourseDto, CreateCourseRequest, UpdateCourseRequest> getMapper() {
        return courseMapper;
    }

    @Override
    protected String getRestUrl() {
        return COURSE_REST_URL;
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Course course = courseService.findById(id);
        List<Long> bindedGroupIds = course.getGroups().stream().map(Group::getId).toList();
        for (Long groupId : bindedGroupIds) {
            objectBinder.unbindCourseFromGroup(id, groupId);
        }
        return super.delete(id);
    }

    @PutMapping("/{id}/groups/{groupId}")
    public ResponseEntity<CourseDto> addGroupToCourse(@PathVariable Long id, @PathVariable Long groupId) {
        objectBinder.bindCourseToGroup(id, groupId);
        Course course = getService().findById(id);
        return ResponseEntity.ok(getMapper().toDto(course));
    }

    @DeleteMapping("/{courseId}/groups/{groupId}")
    public ResponseEntity<CourseDto> removeGroupFromCourse(@PathVariable Long courseId, @PathVariable Long groupId) {
        objectBinder.unbindCourseFromGroup(courseId, groupId);
        Course course = getService().findById(courseId);
        return ResponseEntity.ok(getMapper().toDto(course));
    }

}
