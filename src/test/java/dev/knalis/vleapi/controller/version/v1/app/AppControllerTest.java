package dev.knalis.vleapi.controller.version.v1.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void me_returnsAuthenticationName() throws Exception {
        TestingAuthenticationToken auth = new TestingAuthenticationToken("tester", null);

        mockMvc.perform(get("/api/v1/me").principal(auth))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged in as: tester"));
    }
}
