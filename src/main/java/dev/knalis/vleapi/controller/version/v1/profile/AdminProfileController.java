package dev.knalis.vleapi.controller.version.v1.profile;

import dev.knalis.vleapi.model.entity.user.AdminProfile;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.model.dto.user.AdminProfileUpdateRequest;
import dev.knalis.vleapi.repo.AdminProfileRepo;
import dev.knalis.vleapi.service.intrf.UserService;
import dev.knalis.vleapi.service.impl.UserProfileAssembler;
import dev.knalis.vleapi.model.dto.user.UserExtendedDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "Admin Profile", description = "Manage admin-specific data (department)")
@RestController
@RequestMapping("/api/v1/profiles/admin")
public class AdminProfileController {

    @Autowired private AdminProfileRepo adminProfileRepo;
    @Autowired private UserService userService;
    @Autowired private UserProfileAssembler assembler;

    @Operation(summary = "Get current admin's profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/me")
    public ResponseEntity<UserExtendedDto> myProfile(Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        return adminProfileRepo.findByUserId(user.getId())
                .map(ap -> ResponseEntity.ok(assembler.assemble(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update admin department", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PatchMapping("/{userId}/department")
    public ResponseEntity<UserExtendedDto> updateDepartment(@PathVariable Long userId, @RequestBody AdminProfileUpdateRequest req) {
        User user = userService.findById(userId);
        if (user == null || !user.getRole().name().equals("ADMINISTRATOR")) {
            return ResponseEntity.badRequest().build();
        }
        AdminProfile ap = adminProfileRepo.findByUserId(user.getId()).orElseGet(() -> { AdminProfile created = new AdminProfile(); created.setUser(user); return adminProfileRepo.save(created); });
        ap.setDepartment(req.getDepartment());
        adminProfileRepo.save(ap);
        return ResponseEntity.ok(assembler.assemble(user));
    }

    @Operation(summary = "Update admin profile", description = "Update fields of admin profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Profile updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminProfile.class)))
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/problem+json"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PatchMapping("/{userId}/profile")
    public ResponseEntity<AdminProfile> patch(@PathVariable Long userId, @Valid @RequestBody AdminProfileUpdateRequest req) {
        AdminProfile profile = adminProfileRepo.findByUserId(userId).orElseThrow();
        if (req.getDepartment() != null) profile.setDepartment(req.getDepartment());
        adminProfileRepo.save(profile);
        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "Replace admin profile", description = "Replace all fields of admin profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Profile replaced", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdminProfile.class)))
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/problem+json"))
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/{userId}")
    public ResponseEntity<AdminProfile> put(@PathVariable Long userId, @Valid @RequestBody AdminProfileUpdateRequest req) {
        AdminProfile profile = adminProfileRepo.findByUserId(userId).orElseThrow();
        profile.setDepartment(req.getDepartment());
        adminProfileRepo.save(profile);
        return ResponseEntity.ok(profile);
    }
}
