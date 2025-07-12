package dev.knalis.vleapi.service.intrf;

import dev.knalis.vleapi.model.entity.Group;

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
}
