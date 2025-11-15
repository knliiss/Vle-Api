package dev.knalis.vleapi.controller;

import dev.knalis.vleapi.VleApiApplication;
import dev.knalis.vleapi.model.dto.user.UserCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VleApiApplication.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void createUserValidationFails_whenUsernameTooShort() throws Exception {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("ab");
        req.setPassword("123456");

        String payload = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}
