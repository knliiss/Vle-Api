package dev.knalis.vleapi.service.intrf;

import dev.knalis.vleapi.model.entity.Course;

public interface CourseService extends CRUDService<Course, Long> {

    /**
     * Finds a course by its name.
     *
     * @param name the name of the course
     * @return the course with the specified name, or null if not found
     */
    Course findByName(String name);

    /**
     * Checks if a course with the specified name exists.
     *
     * @param name the name of the course
     * @return true if a course with the specified name exists, false otherwise
     */
    boolean existsByName(String name);
}
