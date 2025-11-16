package dev.knalis.vleapi.controller.version.v1.admin;

import dev.knalis.vleapi.util.ObjectBinder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RelationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RelationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObjectBinder objectBinder;

    @Test
    public void bindUserToGroup_ok() throws Exception {
        mockMvc.perform(post("/api/v1/admin/relations/user/1/group/2"))
                .andExpect(status().isOk());

        verify(objectBinder).bindUserToGroup(1L, 2L);
    }

    @Test
    public void unbindUserFromGroup_noContent() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/relations/user/1/group/2"))
                .andExpect(status().isNoContent());

        verify(objectBinder).unbindUserFromGroup(1L, 2L);
    }
}

