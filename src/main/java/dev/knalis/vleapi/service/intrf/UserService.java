package dev.knalis.vleapi.service.intrf;

import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.user.User;

import java.util.List;

public interface UserService extends CRUDService<User, Long> {
    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return the user with the specified username, or null if not found
     */
    User findByUsername(String username);

    /**
     * Checks if a user with the specified username exists.
     *
     * @param username the username of the user
     * @return true if a user with the specified username exists, false otherwise
     */
    boolean existsByUsername(String username);

    List<Course> findAvailableCoursesForUser(Long userId);
}
