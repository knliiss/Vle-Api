package dev.knalis.vleapi.repo;

import dev.knalis.vleapi.model.entity.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UserRepo extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
