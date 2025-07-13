package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.repo.CourseRepo;
import dev.knalis.vleapi.service.intrf.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends AbstractCRUDService<Course, Long> implements CourseService {

    @Autowired
    private CourseRepo courseRepo;

    @Override
    CrudRepository<Course, Long> getRepository() {
        return courseRepo;
    }

    @Override
    protected Class<Course> getEntityClass() {
        return Course.class;
    }

    @Override
    public Course findByName(String name) {
        return courseRepo.findByName(name).orElseThrow();
    }

    @Override
    public boolean existsByName(String name) {
        return courseRepo.existsByName(name);
    }

    @Override
    public Long getId(Course created) {
        return created.getId();
    }
}
