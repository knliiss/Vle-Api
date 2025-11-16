package dev.knalis.vleapi.controller.version.v1.task;

import dev.knalis.vleapi.model.dto.task.TaskDto;
import dev.knalis.vleapi.model.entity.task.Task;
import dev.knalis.vleapi.service.intrf.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private dev.knalis.vleapi.mapper.impl.TaskEntityMapper taskMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    public void createTask_returnsCreated() throws Exception {
        TaskDto dto = new TaskDto();
        dto.setName("Task1");

        Task entity = new Task();
        entity.setId(4L);
        entity.setName("Task1");

        when(taskMapper.fromCreateRequest(any())).thenReturn(entity);
        when(taskService.create(any())).thenReturn(entity);
        when(taskService.getId(any())).thenReturn(4L);
        when(taskMapper.toDto(any())).thenReturn(dto);

        String payload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/tasks").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Task1"));
    }

    @Test
    public void findAll_returnsList() throws Exception {
        Task entity = new Task();
        entity.setId(4L);
        entity.setName("Task1");

        TaskDto dto = new TaskDto();
        dto.setId(4L);
        dto.setName("Task1");

        when(taskService.findAll()).thenReturn(List.of(entity));
        when(taskMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Task1"));
    }
}
