package dev.knalis.vleapi.controller.version.v1.group;

import dev.knalis.vleapi.model.dto.group.GroupDto;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.service.intrf.GroupService;
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

@WebMvcTest(GroupController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private dev.knalis.vleapi.mapper.impl.GroupEntityMapper groupMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    public void createGroup_returnsCreated() throws Exception {
        GroupDto dto = new GroupDto();
        dto.setName("G1");

        Group entity = new Group();
        entity.setId(5L);
        entity.setName("G1");

        when(groupMapper.fromCreateRequest(any())).thenReturn(entity);
        when(groupService.create(any())).thenReturn(entity);
        when(groupService.getId(any())).thenReturn(5L);
        when(groupMapper.toDto(any())).thenReturn(dto);

        String payload = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/groups").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("G1"));
    }

    @Test
    public void findAll_returnsList() throws Exception {
        Group entity = new Group();
        entity.setId(5L);
        entity.setName("G1");

        GroupDto dto = new GroupDto();
        dto.setId(5L);
        dto.setName("G1");

        when(groupService.findAll()).thenReturn(List.of(entity));
        when(groupMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("G1"));
    }
}
