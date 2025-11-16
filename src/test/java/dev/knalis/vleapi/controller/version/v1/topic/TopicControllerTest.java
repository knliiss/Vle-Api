package dev.knalis.vleapi.controller.version.v1.topic;

import dev.knalis.vleapi.model.dto.topic.TopicDto;
import dev.knalis.vleapi.model.entity.Topic;
import dev.knalis.vleapi.service.intrf.TopicService;
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

@WebMvcTest(TopicController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TopicService topicService;

    @MockBean
    private dev.knalis.vleapi.mapper.impl.TopicEntityMapper topicMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    public void createTopic_returnsCreated() throws Exception {
        TopicDto dto = new TopicDto();
        dto.setName("T1");

        Topic entity = new Topic();
        entity.setId(3L);
        entity.setName("T1");

        when(topicMapper.fromCreateRequest(any())).thenReturn(entity);
        when(topicService.create(any())).thenReturn(entity);
        when(topicService.getId(any())).thenReturn(3L);
        when(topicMapper.toDto(any())).thenReturn(dto);

        String payload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/topics").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("T1"));
    }

    @Test
    public void findAll_returnsList() throws Exception {
        Topic entity = new Topic();
        entity.setId(3L);
        entity.setName("T1");

        TopicDto dto = new TopicDto();
        dto.setId(3L);
        dto.setName("T1");

        when(topicService.findAll()).thenReturn(List.of(entity));
        when(topicMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("T1"));
    }
}
