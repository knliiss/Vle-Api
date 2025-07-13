package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.repo.GroupRepo;
import dev.knalis.vleapi.service.intrf.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl extends AbstractCRUDService<Group, Long> implements GroupService {

    @Autowired
    private GroupRepo groupRepo;

    @Override
    CrudRepository<Group, Long> getRepository() {
        return groupRepo;
    }

    @Override
    protected Class<Group> getEntityClass() {
        return Group.class;
    }

    @Override
    public Group findByName(String name) {
        return groupRepo.findByName(name).orElseThrow();
    }

    @Override
    public boolean existsByName(String name) {
        return groupRepo.existsByName(name);
    }

    @Override
    public Long getId(Group created) {
        return created.getId();
    }
}
