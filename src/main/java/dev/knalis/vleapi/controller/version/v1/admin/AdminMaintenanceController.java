package dev.knalis.vleapi.controller.version.v1.admin;

import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.repo.UserRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "Admin Maintenance", description = "Administrative maintenance operations")
@RestController
@RequestMapping("/api/v1/admin/maintenance")
public class AdminMaintenanceController {

    @Autowired private UserRepo userRepo;

    @Operation(summary = "List usernames that have case-insensitive duplicates", description = "Returns groups of users that collide when usernames are lowercased. ADMIN only.")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/duplicates")
    public ResponseEntity<Map<String, List<User>>> listDuplicateUsernames() {
        List<User> all = new ArrayList<>();
        userRepo.findAll().forEach(all::add);
        Map<String, List<User>> grouped = all.stream().collect(Collectors.groupingBy(u -> u.getUsername().toLowerCase()));
        Map<String, List<User>> duplicates = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return ResponseEntity.ok(duplicates);
    }

    @Operation(summary = "Remove duplicate users keeping the first in each group", description = "Deletes users that share username ignoring case, keeping one. Use with caution. ADMIN only.")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/duplicates/cleanup")
    public ResponseEntity<Map<String, List<Long>>> cleanupDuplicates() {
        List<User> all = new ArrayList<>();
        userRepo.findAll().forEach(all::add);
        Map<String, List<User>> grouped = all.stream().collect(Collectors.groupingBy(u -> u.getUsername().toLowerCase()));
        Map<String, List<Long>> removed = new HashMap<>();
        grouped.entrySet().stream().filter(e -> e.getValue().size() > 1).forEach(e -> {
            List<User> users = e.getValue();
            users.sort(Comparator.comparing(User::getId));
            List<User> toRemove = users.subList(1, users.size());
            List<Long> ids = toRemove.stream().map(User::getId).collect(Collectors.toList());
            ids.forEach(id -> userRepo.deleteById(id));
            removed.put(e.getKey(), ids);
        });
        return ResponseEntity.ok(removed);
    }
}

