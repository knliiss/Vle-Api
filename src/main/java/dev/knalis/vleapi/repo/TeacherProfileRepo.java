package dev.knalis.vleapi.repo;

import dev.knalis.vleapi.model.entity.user.TeacherProfile;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeacherProfileRepo extends CrudRepository<TeacherProfile, Long> {
    Optional<TeacherProfile> findByUserId(Long userId);
}

