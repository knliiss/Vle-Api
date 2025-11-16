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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        User user = userService.findByUsername(auth.getName());
        return teacherProfileRepo.findByUserId(user.getId())
                .map(tp -> ResponseEntity.ok(assembler.assemble(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update teacher academic title", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR') or (hasRole('TEACHER') and principal.username == @userService.findById(#userId).username)")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserExtendedDto> updateAcademicTitle(@PathVariable Long userId, @RequestBody TeacherProfileUpdateRequest req) {
        User user = userService.findById(userId);
        if (!user.getRole().name().equals("TEACHER")) {
            return ResponseEntity.badRequest().build();
        }
        TeacherProfile tp = teacherProfileRepo.findByUserId(user.getId()).orElseGet(() -> { TeacherProfile created = new TeacherProfile(); created.setUser(user); return teacherProfileRepo.save(created); });
        tp.setAcademicTitle(req.getAcademicTitle());
        teacherProfileRepo.save(tp);
        return ResponseEntity.ok(assembler.assemble(user));
    }
}

