package dev.knalis.vleapi.service.intrf;

import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.user.User;

import java.util.List;

public interface UserService extends CRUDService<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean hasAnyUser();
    List<Course> findAvailableCoursesForUser(Long userId);
    List<Course> findCoursesForTeacher(Long teacherId);
}
