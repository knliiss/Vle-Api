package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.exception.custom.DuplicateEntityException;
import dev.knalis.vleapi.exception.custom.UserNotHaveGroupException;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.repo.UserRepo;
import dev.knalis.vleapi.repo.mongo.FileSubmissionDocRepo;
import dev.knalis.vleapi.repo.mongo.TestSubmissionDocRepo;
import dev.knalis.vleapi.service.intrf.UserService;
import dev.knalis.vleapi.model.entity.user.StudentProfile;
import dev.knalis.vleapi.model.entity.user.TeacherProfile;
import dev.knalis.vleapi.model.entity.user.AdminProfile;
import dev.knalis.vleapi.repo.StudentProfileRepo;
import dev.knalis.vleapi.repo.TeacherProfileRepo;
import dev.knalis.vleapi.repo.AdminProfileRepo;
import dev.knalis.vleapi.repo.CourseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl extends AbstractCRUDService<User, Long> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FileSubmissionDocRepo fileSubmissionDocRepo;

    @Autowired
    private TestSubmissionDocRepo testSubmissionDocRepo;

    @Autowired
    private StudentProfileRepo studentProfileRepo;

    @Autowired
    private TeacherProfileRepo teacherProfileRepo;

    @Autowired
    private AdminProfileRepo adminProfileRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Override
    public User update(User object) {
        if (object.getUsername() != null) {
            object.setUsername(object.getUsername().trim().toLowerCase());
        }
        if (object.getPassword() != null && !object.getPassword().isEmpty()) {
            object.setPassword(passwordEncoder.encode(object.getPassword()));
        }
        User existing = userRepo.findById(object.getId()).orElse(null);
        if (existing != null) {
            object.setRole(existing.getRole());
        }
        return super.update(object);
    }

    @Override
    public Long getId(User created) {
        return created.getId();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "userByUsername", allEntries = true),
            @CacheEvict(cacheNames = "availableCourses", allEntries = true),
            @CacheEvict(cacheNames = "coursesByTeacher", allEntries = true)
    })
    public User create(User object) {
        if (object.getUsername() != null) {
            object.setUsername(object.getUsername().trim().toLowerCase());
        }
        if (existsByUsername(object.getUsername())) {
            throw new DuplicateEntityException("Username is already taken: " + object.getUsername());
        }
        if (object.getPassword() != null && !object.getPassword().isEmpty()) {
            object.setPassword(passwordEncoder.encode(object.getPassword()));
        }
        User saved = super.create(object);
        switch (saved.getRole()) {
            case STUDENT -> {
                StudentProfile sp = new StudentProfile();
                sp.setUser(saved);
                studentProfileRepo.save(sp);
            }
            case TEACHER -> {
                TeacherProfile tp = new TeacherProfile();
                tp.setUser(saved);
                tp.setAcademicTitle(null);
                teacherProfileRepo.save(tp);
            }
            case ADMINISTRATOR -> {
                AdminProfile ap = new AdminProfile();
                ap.setUser(saved);
                ap.setDepartment(null);
                adminProfileRepo.save(ap);
            }
        }
        return saved;
    }

    @Override
    CrudRepository<User, Long> getRepository() {
        return userRepo;
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    @Cacheable(cacheNames = "userByUsername", key = "#username")
    public User findByUsername(String username) {
        return userRepo.findFirstByUsernameIgnoreCase(username).orElseGet(() -> userRepo.findByUsernameIgnoreCase(username).stream().findFirst().orElseThrow());
    }

    @Override
    @Cacheable(cacheNames = "availableCourses", key = "#userId")
    public List<Course> findAvailableCoursesForUser(Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        Optional<StudentProfile> sp = studentProfileRepo.findByUserId(user.getId());
        if (sp.isEmpty() || sp.get().getGroup() == null) {
            throw new UserNotHaveGroupException("User " + userId + " does not belong to any group");
        }
        return sp.get().getGroup().getCourses();
    }

    @Override
    @Cacheable(cacheNames = "coursesByTeacher", key = "#teacherId")
    public List<Course> findCoursesForTeacher(Long teacherId) {
        return StreamSupport.stream(courseRepo.findAll().spliterator(), false)
                .filter(course -> course.getTeachers().stream().anyMatch(t -> t.getId().equals(teacherId)))
                .toList();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "userByUsername", allEntries = true),
            @CacheEvict(cacheNames = "availableCourses", allEntries = true),
            @CacheEvict(cacheNames = "coursesByTeacher", allEntries = true)
    })
    public void delete(Long id) {
        try {
            fileSubmissionDocRepo.deleteAll(fileSubmissionDocRepo.findByUserId(id));
        } catch (Exception e) {
            log.warn("Failed to delete file submissions for user {} from MongoDB: {}", id, e.getMessage());
        }
        try {
            testSubmissionDocRepo.deleteAll(testSubmissionDocRepo.findByUserId(id));
        } catch (Exception e) {
            log.warn("Failed to delete test submissions for user {} from MongoDB: {}", id, e.getMessage());
        }
        CourseRepo repo = courseRepo;
        StreamSupport.stream(repo.findAll().spliterator(), false)
            .filter(course -> course.getTeachers().stream().anyMatch(t -> t.getId().equals(id)))
            .forEach(course -> {
                course.getTeachers().removeIf(t -> t.getId().equals(id));
                repo.save(course);
            });
        teacherProfileRepo.findByUserId(id).ifPresent(teacherProfileRepo::delete);
        studentProfileRepo.findByUserId(id).ifPresent(studentProfileRepo::delete);
        adminProfileRepo.findByUserId(id).ifPresent(adminProfileRepo::delete);
        getRepository().deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepo.existsByUsernameIgnoreCase(username);
    }

    @Override
    public boolean hasAnyUser() {
        return userRepo.count() > 0;
    }
}
