package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.Topic;
import dev.knalis.vleapi.model.entity.task.Task;
import dev.knalis.vleapi.repo.CourseRepo;
import dev.knalis.vleapi.service.intrf.CourseService;
import dev.knalis.vleapi.service.intrf.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
public class CourseServiceImpl extends AbstractCRUDService<Course, Long> implements CourseService {

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private TaskService taskService;

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
    public Double getGradeForCourse(Long courseId, Long userId) {
        Course course = courseRepo.findById(courseId).orElseThrow();
        double sum = 0.0;
        int count = 0;
        if (course.getTopics() != null) {
            for (Topic t : course.getTopics()) {
                if (t.getTasks() == null) continue;
                for (Task task : t.getTasks()) {
                    Double g = taskService.getGrade(task.getId(), userId);
                    if (g != null) {
                        sum += g;
                        count++;
                    }
                }
            }
        }
        if (count == 0) return null;
        return sum / count;
    }

    @Override
    public Long getId(Course created) {
        return created.getId();
    }
}
