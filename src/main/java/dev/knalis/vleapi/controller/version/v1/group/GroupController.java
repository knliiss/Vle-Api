package dev.knalis.vleapi.controller.version.v1.group;

import dev.knalis.vleapi.controller.AbstractCRUDController;
import dev.knalis.vleapi.mapper.intrf.ObjectMapper;
import dev.knalis.vleapi.model.dto.group.GroupDto;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.service.intrf.CRUDService;
import dev.knalis.vleapi.service.intrf.GroupService;
import dev.knalis.vleapi.mapper.impl.GroupEntityMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;

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

    @Operation(summary = "Create a group", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize(HAS_ADMIN)
    @PostMapping
    @Override
    public ResponseEntity<GroupDto> create(@Valid @RequestBody GroupDto request) {
        return super.create(request);
    }

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

    @Operation(summary = "Get all courses for a group", description = "Returns all courses linked to the group", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of courses", content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> listCourses(@PathVariable Long id) {
        Group group = groupService.findById(id);
        return ResponseEntity.ok(group.getCourses());
    }

    @Operation(summary = "Get all students for a group", description = "Returns all students in the group", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "List of students", content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}/users")
    public ResponseEntity<?> listStudents(@PathVariable Long id) {
        Group group = groupService.findById(id);
        return ResponseEntity.ok(groupService.findStudentsInGroup(id));
    }

}
