package dev.knalis.vleapi.controller.version.v1.auth;

import dev.knalis.vleapi.model.dto.auth.AuthRequest;
import dev.knalis.vleapi.model.dto.auth.AuthResponse;
import dev.knalis.vleapi.model.dto.auth.TokenPairResponse;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.JwtService;
import dev.knalis.vleapi.service.RefreshTokenService;
import dev.knalis.vleapi.service.intrf.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @MockBean
    private dev.knalis.vleapi.mapper.impl.UserMapper userMapper;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    public void login_returnsTokens() throws Exception {
        AuthRequest req = new AuthRequest();
        req.setUsername("tester");
        req.setPassword("password");

        when(jwtService.generateAccessToken(any())).thenReturn("access");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh");

        String payload = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isOk());
    }

}

