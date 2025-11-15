package dev.knalis.vleapi.controller.version.v1.auth;

import dev.knalis.vleapi.mapper.impl.UserMapper;
import dev.knalis.vleapi.model.dto.auth.AuthRequest;
import dev.knalis.vleapi.model.dto.auth.AuthResponse;
import dev.knalis.vleapi.model.dto.user.UserCreateRequest;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.JwtService;
import dev.knalis.vleapi.service.intrf.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import jakarta.validation.Valid;

@Tag(name = "Auth", description = "Authentication endpoints: login and register")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "JWT token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
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

    @Operation(summary = "Register a new user and return JWT token")
    @ApiResponse(responseCode = "200", description = "JWT token (for new user)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserCreateRequest userCreateRequest) {

        if (userService.existsByUsername(userCreateRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new AuthResponse("Username already exists"));
        }

        User createdUser = userService.create(userMapper.fromCreateRequest(userCreateRequest));

        final String jwtToken = jwtService.generateToken(createdUser.getUsername());
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

}
