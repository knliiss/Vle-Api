package dev.knalis.vleapi.controller.version.v1.auth;

import dev.knalis.vleapi.mapper.impl.UserMapper;
import dev.knalis.vleapi.model.dto.auth.AuthRequest;
import dev.knalis.vleapi.model.dto.auth.AuthResponse;
import dev.knalis.vleapi.model.dto.auth.TokenPairResponse;
import dev.knalis.vleapi.model.dto.user.UserCreateRequest;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.security.LoginRateLimiter;
import dev.knalis.vleapi.service.JwtService;
import dev.knalis.vleapi.service.RefreshTokenService;
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
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.Optional;

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
    private final RefreshTokenService refreshTokenService;
    private final LoginRateLimiter loginRateLimiter;

    @Operation(summary = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "JWT token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenPairResponse.class)))
    @PostMapping("/login")
    public ResponseEntity<TokenPairResponse> login(@RequestBody AuthRequest authRequest, HttpServletRequest httpRequest) {

        String key = httpRequest.getRemoteAddr();
        if (key == null || key.isBlank()) key = authRequest.getUsername();

        if (!loginRateLimiter.isAllowed(key)) {
            return ResponseEntity.status(429).build();
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
        } catch (Exception ex) {
            loginRateLimiter.onFailure(key);
            return ResponseEntity.status(401).build();
        }

        // success -> reset limiter
        loginRateLimiter.onSuccess(key);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        if (userDetails == null) {
            return ResponseEntity.badRequest().build();
        }

        final String accessToken = jwtService.generateAccessToken(userDetails.getUsername());
        final String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());

        Instant expiresAt = Instant.now().plusMillis(1000L * 60 * 60 * 24 * 7);
        refreshTokenService.create(userDetails.getUsername(), refreshToken, expiresAt);

        return ResponseEntity.ok(new TokenPairResponse(accessToken, refreshToken));
    }

    @Operation(summary = "Refresh access token using refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<TokenPairResponse> refresh(@RequestBody AuthResponse req) {
        String refreshToken = req.getToken();
        Optional<dev.knalis.vleapi.model.entity.RefreshToken> stored = refreshTokenService.findByToken(refreshToken);
        if (stored.isEmpty() || stored.get().isRevoked() || stored.get().getExpiresAt().isBefore(Instant.now())) {
            return ResponseEntity.status(401).build();
        }

        String username = jwtService.extractUsername(refreshToken);
        final String newAccess = jwtService.generateAccessToken(username);
        final String newRefresh = jwtService.generateRefreshToken(username);

        refreshTokenService.revoke(stored.get());
        Instant expiresAt = Instant.now().plusMillis(1000L * 60 * 60 * 24 * 7);
        refreshTokenService.create(username, newRefresh, expiresAt);

        return ResponseEntity.ok(new TokenPairResponse(newAccess, newRefresh));
    }

    @Operation(summary = "Register a new user and return JWT token")
    @ApiResponse(responseCode = "200", description = "JWT token (for new user)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenPairResponse.class)))
    @PostMapping("/register")
    public ResponseEntity<TokenPairResponse> register(@Valid @RequestBody UserCreateRequest userCreateRequest) {

        if (userService.existsByUsername(userCreateRequest.getUsername())) {
            return ResponseEntity.badRequest().build();
        }

        User createdUser = userService.create(userMapper.fromCreateRequest(userCreateRequest));

        final String accessToken = jwtService.generateAccessToken(createdUser.getUsername());
        final String refreshToken = jwtService.generateRefreshToken(createdUser.getUsername());
        Instant expiresAt = Instant.now().plusMillis(1000L * 60 * 60 * 24 * 7);
        refreshTokenService.create(createdUser.getUsername(), refreshToken, expiresAt);

        return ResponseEntity.ok(new TokenPairResponse(accessToken, refreshToken));
    }

    @Operation(summary = "Logout (revoke refresh token)")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody AuthResponse req) {
        String refreshToken = req.getToken();
        Optional<dev.knalis.vleapi.model.entity.RefreshToken> stored = refreshTokenService.findByToken(refreshToken);
        stored.ifPresent(refreshTokenService::revoke);
        return ResponseEntity.noContent().build();
    }

}
