package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.model.entity.Topic;
import dev.knalis.vleapi.repo.TopicRepo;
import dev.knalis.vleapi.service.intrf.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl extends AbstractCRUDService<Topic, Long> implements TopicService {

    @Autowired
    private TopicRepo topicRepo;

    @Override
    CrudRepository<Topic, Long> getRepository() {
        return topicRepo;
    }

    @Override
    public Long getId(Topic created) {
        return created.getId();
    }
}
