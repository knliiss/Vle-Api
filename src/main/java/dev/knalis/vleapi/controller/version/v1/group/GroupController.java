package dev.knalis.vleapi.controller.version.v1.group;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.impl.GroupEntityMapper;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.group.GroupDto;
import dev.knalis.vleapi.model.dto.group.GroupStudentDto;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.model.entity.user.StudentProfile;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

import static dev.knalis.vleapi.security.Spel.HAS_ADMIN;

@Tag(name = "Groups", description = "Group management")
@RestController
@RequestMapping("/api/v1/groups")
public class GroupController extends AbstractCRUDController<Group, GroupDto, GroupDto, GroupDto, Long> {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupEntityMapper groupMapper;

    @Override
    protected CRUDService<Group, Long> getService() { return groupService; }

    @Override
    protected ObjectMapper<Group, GroupDto, GroupDto, GroupDto> getMapper() { return groupMapper; }

    @Override
    protected String getRestUrl() { return "groups"; }
    
    @Operation(summary = "Update a group", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize(HAS_ADMIN)
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<GroupDto> update(@PathVariable Long id, @Valid @RequestBody GroupDto request) {
        return super.update(id, request);
    }

    @Operation(summary = "Delete a group", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize(HAS_ADMIN)
    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return super.delete(id);
    }
    
    @Operation(summary = "Create a group", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "Group created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupDto.class)))
    @PreAuthorize(HAS_ADMIN)
    @Override
    @PostMapping
    public ResponseEntity<GroupDto> create(@Valid @RequestBody GroupDto request) {
        if (request.getYear() == null) {
            request.setYear(Year.now().getValue());
        }
        return super.create(request);
    }

    @Operation(summary = "Get all courses for a group", description = "Returns all courses linked to the group", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of courses", content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> listCourses(@PathVariable Long id) {
        Group group = groupService.findById(id);
        return ResponseEntity.ok(group.getCourses());
    }

    @Operation(summary = "Get all students for a group", description = "Returns all students in the group", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of students", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GroupStudentDto.class)))
    @GetMapping("/{id}/users")
    public ResponseEntity<List<GroupStudentDto>> listStudents(@PathVariable Long id) {
        List<StudentProfile> students = groupService.findStudentsInGroup(id);
        List<GroupStudentDto> dtos = students.stream().map(this::toGroupStudentDto).toList();
        return ResponseEntity.ok(dtos);
    }

    private GroupStudentDto toGroupStudentDto(StudentProfile profile) {
        GroupStudentDto dto = new GroupStudentDto();
        dto.setId(profile.getId());
        if (profile.getUser() != null) {
            dto.setUsername(profile.getUser().getUsername());
            dto.setFio(profile.getUser().getFio());
            dto.setAvatarUrl(profile.getUser().getAvatarUrl());
        }
        return dto;
    }

}
