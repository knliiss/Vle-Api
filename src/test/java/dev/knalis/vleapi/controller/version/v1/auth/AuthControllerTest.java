package dev.knalis.vleapi.controller.version.v1.auth;

import dev.knalis.vleapi.model.dto.auth.AuthRequest;
import dev.knalis.vleapi.model.dto.user.UserCreateRequest;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.JwtService;
import dev.knalis.vleapi.service.intrf.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @MockBean
    private dev.knalis.vleapi.mapper.impl.UserMapper userMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    public void register_returnsToken_whenSuccess() throws Exception {
        UserCreateRequest req = new UserCreateRequest();
        req.setUsername("newuser");
        req.setPassword("password123");

        User created = new User();
        created.setId(11L);
        created.setUsername("newuser");

        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userMapper.fromCreateRequest(req)).thenReturn(created);
        when(userService.create(created)).thenReturn(created);
        when(jwtService.generateToken(anyString())).thenReturn("token123");

        String payload = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    public void login_badCredentials_returnsBadRequest() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setUsername("x");
        req.setPassword("y");

        // simulate authentication throwing exception by leaving authenticationManager mock default (it will do nothing)
        // but userDetailsService returns null to force bad request path
        when(userDetailsService.loadUserByUsername("x")).thenReturn(null);

        String payload = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isBadRequest());
    }
}

