package dev.knalis.vleapi.controller.version.v1.profile;

import dev.knalis.vleapi.model.entity.user.StudentProfile;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.model.dto.user.StudentProfileUpdateRequest;
import dev.knalis.vleapi.repo.StudentProfileRepo;
import dev.knalis.vleapi.service.intrf.UserService;
import dev.knalis.vleapi.model.dto.user.UserExtendedDto;
import dev.knalis.vleapi.service.impl.UserProfileAssembler;
import dev.knalis.vleapi.service.intrf.GroupService;
import dev.knalis.vleapi.model.entity.Group;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Student Profile", description = "Manage student-specific data (group assignment)")
@RestController
@RequestMapping("/api/v1/profiles/student")
public class StudentProfileController {

    @Autowired private StudentProfileRepo studentProfileRepo;
    @Autowired private UserService userService;
    @Autowired private GroupService groupService;
    @Autowired private UserProfileAssembler assembler;

    @Operation(summary = "Get current student's profile", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/me")
    public ResponseEntity<UserExtendedDto> myProfile(Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        return studentProfileRepo.findByUserId(user.getId())
                .map(sp -> ResponseEntity.ok(assembler.assemble(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Assign or unassign group to student", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMINISTRATOR') or (hasRole('STUDENT') and principal.username == @userService.findById(#userId).username)")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserExtendedDto> updateGroup(@PathVariable Long userId, @RequestBody StudentProfileUpdateRequest req) {
        User user = userService.findById(userId);
        if (!user.getRole().name().equals("STUDENT")) {
            return ResponseEntity.badRequest().build();
        }
        StudentProfile sp = studentProfileRepo.findByUserId(user.getId()).orElseGet(() -> {
            StudentProfile created = new StudentProfile(); created.setUser(user); return studentProfileRepo.save(created);
        });
        if (req.getGroupId() == null) {
            sp.setGroup(null);
        } else {
            Group g = groupService.findById(req.getGroupId());
            sp.setGroup(g);
        }
        studentProfileRepo.save(sp);
        return ResponseEntity.ok(assembler.assemble(user));
    }
}

