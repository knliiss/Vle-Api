package dev.knalis.vleapi.service.intrf;

import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.model.entity.user.StudentProfile;

import java.util.List;

public interface GroupService extends CRUDService<Group, Long> {

    /**
     * Finds a group by its name.
     *
     * @param name the name of the group
     * @return the group with the specified name, or null if not found
     */
    Group findByName(String name);

    /**
     * Checks if a group with the specified name exists.
     *
     * @param name the name of the group
     * @return true if a group with the specified name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Получить студентов по id группы.
     *
     * @param groupId идентификатор группы
     * @return список студентов, принадлежащих к группе с указанным идентификатором
     */
    List<StudentProfile> findStudentsInGroup(Long groupId);
}
