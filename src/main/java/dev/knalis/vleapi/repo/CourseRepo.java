package dev.knalis.vleapi.repo;

import dev.knalis.vleapi.model.entity.Course;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepo extends CrudRepository<Course, Long> {
    Optional<Course> findByName(String name);

    boolean existsByName(String name);

}
