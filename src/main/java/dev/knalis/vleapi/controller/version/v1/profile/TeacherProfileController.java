package dev.knalis.vleapi.controller.version.v1.profile;

import dev.knalis.vleapi.model.entity.user.TeacherProfile;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.model.dto.user.TeacherProfileUpdateRequest;
import dev.knalis.vleapi.repo.TeacherProfileRepo;
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
import org.springframework.http.ProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import static dev.knalis.vleapi.security.Spel.IS_SELF_BY_USERID_PARAM;

@Tag(name = "Teacher Profile", description = "Manage teacher-specific data (academic title)")
@RestController
@RequestMapping("/api/v1/profiles/teacher")
public class TeacherProfileController {

    @Autowired private TeacherProfileRepo teacherProfileRepo;
    @Autowired private UserService userService;
    @Autowired private UserProfileAssembler assembler;

    @Operation(summary = "Get current teacher's profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/me")
    public ResponseEntity<UserExtendedDto> myProfile(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body(null);
        }
        User user = userService.findByUsername(auth.getName());
        return teacherProfileRepo.findByUserId(user.getId())
                .map(tp -> ResponseEntity.ok(assembler.assemble(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update teacher academic title", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR') or (hasRole('TEACHER') and " + IS_SELF_BY_USERID_PARAM + ")")
    @PatchMapping("/{userId}/academicTitle")
    public ResponseEntity<?> updateAcademicTitle(@PathVariable Long userId, @RequestBody TeacherProfileUpdateRequest req) {
        User user = userService.findById(userId);
        if (!"TEACHER".equals(user.getRole().name())) {
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            pd.setDetail("User is not a teacher");
            return ResponseEntity.badRequest().body(pd);
        }
        TeacherProfile tp = teacherProfileRepo.findByUserId(userId).orElseGet(() -> {
            TeacherProfile created = new TeacherProfile();
            created.setUser(user);
            return teacherProfileRepo.save(created);
        });
        tp.setAcademicTitle(req.getAcademicTitle());
        teacherProfileRepo.save(tp);
        return ResponseEntity.ok(assembler.assemble(user));
    }

    @Operation(summary = "Update teacher profile", description = "Update fields of teacher profile (workPhone, department, scientificDegree, academicTitle)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Profile updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherProfile.class)))
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/problem+json"))
    @PreAuthorize("hasRole('ADMINISTRATOR') or (hasRole('TEACHER') and " + IS_SELF_BY_USERID_PARAM + ")")
    @PatchMapping("/{userId}")
    public ResponseEntity<?> patch(@PathVariable Long userId, @Valid @RequestBody TeacherProfileUpdateRequest req) {
        User user = userService.findById(userId);
        if (!"TEACHER".equals(user.getRole().name())) {
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            pd.setDetail("User is not a teacher");
            return ResponseEntity.badRequest().body(pd);
        }
        TeacherProfile profile = teacherProfileRepo.findByUserId(userId).orElseGet(() -> {
            TeacherProfile created = new TeacherProfile();
            created.setUser(user);
            return teacherProfileRepo.save(created);
        });
        if (req.getWorkPhone() != null) profile.setWorkPhone(req.getWorkPhone());
        if (req.getDepartment() != null) profile.setDepartment(req.getDepartment());
        if (req.getScientificDegree() != null) profile.setScientificDegree(req.getScientificDegree());
        if (req.getAcademicTitle() != null) profile.setAcademicTitle(req.getAcademicTitle());
        teacherProfileRepo.save(profile);
        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "Replace teacher profile", description = "Replace all fields of teacher profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Profile replaced", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherProfile.class)))
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/problem+json"))
    @PreAuthorize("hasRole('ADMINISTRATOR') or (hasRole('TEACHER') and " + IS_SELF_BY_USERID_PARAM + ")")
    @PutMapping("/{userId}")
    public ResponseEntity<?> put(@PathVariable Long userId, @Valid @RequestBody TeacherProfileUpdateRequest req) {
        User user = userService.findById(userId);
        if (!"TEACHER".equals(user.getRole().name())) {
            ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            pd.setDetail("User is not a teacher");
            return ResponseEntity.badRequest().body(pd);
        }
        TeacherProfile profile = teacherProfileRepo.findByUserId(userId).orElseGet(() -> {
            TeacherProfile created = new TeacherProfile();
            created.setUser(user);
            return teacherProfileRepo.save(created);
        });
        profile.setWorkPhone(req.getWorkPhone());
        profile.setDepartment(req.getDepartment());
        profile.setScientificDegree(req.getScientificDegree());
        profile.setAcademicTitle(req.getAcademicTitle());
        teacherProfileRepo.save(profile);
        return ResponseEntity.ok(profile);
    }
}
