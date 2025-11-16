package dev.knalis.vleapi.repo;

import dev.knalis.vleapi.model.entity.user.AdminProfile;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AdminProfileRepo extends CrudRepository<AdminProfile, Long> {
    Optional<AdminProfile> findByUserId(Long userId);
}

