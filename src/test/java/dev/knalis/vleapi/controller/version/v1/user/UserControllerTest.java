package dev.knalis.vleapi.controller.version.v1.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.service.intrf.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private dev.knalis.vleapi.mapper.impl.UserEntityMapper userMapper;

    @Test
    public void getAvailableCourses_returnsCourses() throws Exception {
        Course c = new Course();
        c.setId(10L);
        c.setName("TestCourse");

        when(userService.findAvailableCoursesForUser(1L)).thenReturn(List.of(c));

        mockMvc.perform(get("/api/v1/users/1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }
}

