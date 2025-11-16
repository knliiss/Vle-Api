package dev.knalis.vleapi.controller.version.v1.course;

import dev.knalis.vleapi.model.dto.course.CourseDto;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.service.intrf.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private dev.knalis.vleapi.mapper.impl.CourseEntityMapper courseMapper;

    @Test
    public void findAll_returnsList() throws Exception {
        Course entity = new Course();
        entity.setId(2L);
        entity.setName("MyCourse");

        CourseDto dto = new CourseDto();
        dto.setId(2L);
        dto.setName("MyCourse");

        when(courseService.findAll()).thenReturn(List.of(entity));
        when(courseMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("MyCourse"));
    }
}
