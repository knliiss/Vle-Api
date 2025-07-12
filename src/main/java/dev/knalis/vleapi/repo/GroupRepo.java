package dev.knalis.vleapi.repo;

import dev.knalis.vleapi.model.entity.Group;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepo extends CrudRepository<Group, Long> {

    boolean existsByName(String name);

    Optional<Group> findByName(String name);
}
