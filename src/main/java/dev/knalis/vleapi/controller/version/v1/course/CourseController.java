package dev.knalis.vleapi.controller.version.v1.course;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.course.CourseDto;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.CourseService;
import dev.knalis.vleapi.mapper.impl.CourseEntityMapper;
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

@Tag(name = "Courses", description = "Course management")
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController extends AbstractCRUDController<Course, CourseDto, CourseDto, CourseDto, Long> {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseEntityMapper courseMapper;

    @Override
    protected CRUDService<Course, Long> getService() { return courseService; }

    @Override
    protected ObjectMapper<Course, CourseDto, CourseDto, CourseDto> getMapper() { return courseMapper; }

    @Override
    protected String getRestUrl() { return "courses"; }

    @Operation(summary = "Create a course", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @Override
    public ResponseEntity<CourseDto> create(@Valid @RequestBody CourseDto request) {
        return super.create(request);
    }

}
