package dev.knalis.vleapi.service.impl;

import dev.knalis.vleapi.exception.custom.UserNotHaveGroupException;
import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.repo.UserRepo;
import dev.knalis.vleapi.service.intrf.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends AbstractCRUDService<User, Long> implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User update(User object) {
        if (object.getPassword() != null && !object.getPassword().isEmpty()) {
            object.setPassword(passwordEncoder.encode(object.getPassword()));
        }
        return super.update(object);
    }

    @Override
    public Long getId(User created) {
        return created.getId();
    }

    @Override
    public User create(User object) {
        if (object.getPassword() != null && !object.getPassword().isEmpty()) {
            object.setPassword(passwordEncoder.encode(object.getPassword()));
        }
        return super.create(object);
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
    public User findByUsername(String username) {
        return userRepo.findByUsername(username).orElseThrow();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    @Override
    public List<Course> findAvailableCoursesForUser(Long userId) {
        User user = userRepo.findById(userId).orElseThrow();
        if (user.getGroup() == null) {
            throw new UserNotHaveGroupException("User " + userId + " does not belong to any group");
        }
        return user.getGroup().getCourses();
    }
}
