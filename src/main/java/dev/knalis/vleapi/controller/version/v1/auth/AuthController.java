package dev.knalis.vleapi.controller.version.v1.auth;

import dev.knalis.vleapi.mapper.impl.UserMapper;
import dev.knalis.vleapi.model.dto.auth.AuthRequest;
import dev.knalis.vleapi.model.dto.auth.AuthResponse;
import dev.knalis.vleapi.model.dto.user.CreateUserRequest;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.JwtService;
import dev.knalis.vleapi.service.intrf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        if (userDetails == null) {
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid username or password"));
        }

        final String jwtToken = jwtService.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody CreateUserRequest createUserRequest) {

        if (userService.existsByUsername(createUserRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new AuthResponse("Username already exists"));
        }

        User createdUser = userService.create(userMapper.fromCreateRequest(createUserRequest));

        final String jwtToken = jwtService.generateToken(createdUser.getUsername());
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

}

