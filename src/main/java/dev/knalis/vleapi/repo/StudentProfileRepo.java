package dev.knalis.vleapi.repo;

import dev.knalis.vleapi.model.entity.user.StudentProfile;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StudentProfileRepo extends CrudRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
}

