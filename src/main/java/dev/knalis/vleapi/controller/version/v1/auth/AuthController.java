package dev.knalis.vleapi.controller.version.v1.auth;

import dev.knalis.vleapi.mapper.impl.UserMapper;
import dev.knalis.vleapi.model.dto.auth.AuthRequest;
import dev.knalis.vleapi.model.dto.auth.AuthResponse;
import dev.knalis.vleapi.model.dto.auth.TokenPairResponse;
import dev.knalis.vleapi.model.dto.user.UserCreateRequest;
import dev.knalis.vleapi.model.entity.RefreshToken;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.security.LoginRateLimiter;
import dev.knalis.vleapi.service.JwtService;
import dev.knalis.vleapi.service.RefreshTokenService;
import dev.knalis.vleapi.service.intrf.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
    @Value("${admin.bootstrap-secret:}")
    private String adminBootstrapSecret;
    
    @Operation(summary = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "JWT token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenPairResponse.class)))
    @PostMapping("/login")
    public ResponseEntity<TokenPairResponse> login(@RequestBody AuthRequest authRequest, HttpServletRequest httpRequest) {
        String key = httpRequest.getRemoteAddr();
        if (key == null || key.isBlank()) key = authRequest.getUsername();
        
        if (!loginRateLimiter.isAllowed(key)) {
            return ResponseEntity.status(429).body(null);
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
            return ResponseEntity.status(401).body(null);
        }
        
        loginRateLimiter.onSuccess(key);
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        
        if (userDetails == null) {
            return ResponseEntity.badRequest().body(null);
        }
        
        final String accessToken = jwtService.generateAccessToken(userDetails.getUsername());
        final String refreshToken = jwtService.generateRefreshToken(userDetails.getUsername());
        
        Instant expiresAt = Instant.now().plusMillis(1000L * 60 * 60 * 24 * 7);
        refreshTokenService.create(userDetails.getUsername(), refreshToken, expiresAt);
        
        return ResponseEntity.ok(new TokenPairResponse(accessToken, refreshToken));
    }
    
    @Operation(summary = "Refresh access token using refresh token",
            description = "Use the refresh token to obtain a new access token and refresh token pair. The provided refresh token must be valid and not revoked.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "New token pair", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenPairResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request or token not found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"type\": \"https://http.dev/problems/bad-request\",\"title\": \"Bad request\",\"status\": 400,\"detail\": \"Refresh token not provided or malformed\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized or expired token", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"type\": \"https://http.dev/problems/unauthorized\",\"title\": \"Invalid token\",\"status\": 401,\"detail\": \"Refresh token revoked or expired\"}")))
            })
    @PostMapping("/refresh")
    public ResponseEntity<TokenPairResponse> refresh(@RequestBody AuthResponse req) {
        String refreshToken = req.getToken();
        Optional<RefreshToken> stored = refreshTokenService.findByToken(refreshToken);
        if (stored.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (stored.get().isRevoked() || stored.get().getExpiresAt().isBefore(Instant.now())) {
            return ResponseEntity.status(401).body(null);
        }

        String username = jwtService.extractUsername(refreshToken);
        final String newAccess = jwtService.generateAccessToken(username);
        final String newRefresh = jwtService.generateRefreshToken(username);

        refreshTokenService.revoke(stored.get());
        Instant expiresAt = Instant.now().plusMillis(1000L * 60 * 60 * 24 * 7);
        refreshTokenService.create(username, newRefresh, expiresAt);

        return ResponseEntity.ok(new TokenPairResponse(newAccess, newRefresh));
    }

    @Operation(summary = "Register a new user and return JWT token",
            description = "Register a new user. To create an ADMINISTRATOR account, either the requester must be an administrator or a valid X-Bootstrap-Secret header must be provided.",
            parameters = {
                    @Parameter(ref = "#/components/parameters/X-Bootstrap-Secret")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "JWT token (for new user)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenPairResponse.class))),
                    @ApiResponse(responseCode = "201", description = "JWT token (for new user)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenPairResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registration data", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"type\": \"https://http.dev/problems/bad-request\",\"title\": \"Bad request\",\"status\": 400,\"detail\": \"Invalid registration data\"}"))),
                    @ApiResponse(responseCode = "403", description = "Invalid bootstrap secret or not allowed", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"type\": \"https://http.dev/problems/access-denied\",\"title\": \"Forbidden\",\"status\": 403,\"detail\": \"Cannot register ADMINISTRATOR without admin privileges or valid bootstrap secret\"}"))),
                    @ApiResponse(responseCode = "409", description = "Username is already taken", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = org.springframework.http.ProblemDetail.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"type\": \"https://http.dev/problems/conflict\",\"title\": \"Conflict\",\"status\": 409,\"detail\": \"Username already exists\"}")))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateRequest userCreateRequest, HttpServletRequest httpRequest) {
        if (userService.existsByUsername(userCreateRequest.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Username is already exist.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean requesterIsAdmin = false;
        if (auth != null && auth.isAuthenticated() && auth.getAuthorities() != null) {
            requesterIsAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMINISTRATOR".equals(a.getAuthority()) || "ADMINISTRATOR".equals(a.getAuthority()));
        }

        String provided = httpRequest.getHeader("X-Bootstrap-Secret");
        boolean providedBootstrapMatches = false;
        if (adminBootstrapSecret != null && !adminBootstrapSecret.isBlank() && provided != null) {
            providedBootstrapMatches = provided.equals(adminBootstrapSecret);
        }

        if ("ADMINISTRATOR".equalsIgnoreCase(userCreateRequest.getRole())) {
            // Allow creation of administrator only if the requester is already an admin OR the X-Bootstrap-Secret header matches
            if (!requesterIsAdmin && !providedBootstrapMatches) {
                return ResponseEntity.status(403).body("Cannot register ADMINISTRATOR without admin privileges or valid bootstrap secret");
            }
        }
        
        try {
            User createdUser = userService.create(userMapper.fromCreateRequest(userCreateRequest));
            final String accessToken = jwtService.generateAccessToken(createdUser.getUsername());
            final String refreshToken = jwtService.generateRefreshToken(createdUser.getUsername());
            Instant expiresAt = Instant.now().plusMillis(1000L * 60 * 60 * 24 * 7);
            refreshTokenService.create(createdUser.getUsername(), refreshToken, expiresAt);
            
            return ResponseEntity.ok(new TokenPairResponse(accessToken, refreshToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid registration data");
        }
    }
    
    
    @Operation(summary = "Logout (revoke refresh token)")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody AuthResponse req) {
        String refreshToken = req.getToken();
        Optional<RefreshToken> stored = refreshTokenService.findByToken(refreshToken);
        if (stored.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        stored.ifPresent(refreshTokenService::revoke);
        return ResponseEntity.noContent().build();
    }
    
}
