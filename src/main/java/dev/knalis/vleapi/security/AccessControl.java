package dev.knalis.vleapi.security;

import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.Topic;
import dev.knalis.vleapi.model.entity.task.Task;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.repo.CourseRepo;
import dev.knalis.vleapi.repo.TaskRepo;
import dev.knalis.vleapi.repo.TopicRepo;
import dev.knalis.vleapi.repo.UserRepo;
import dev.knalis.vleapi.repo.StudentProfileRepo;
import dev.knalis.vleapi.model.entity.user.StudentProfile;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.model.entity.user.Role;

@Component("accessControl")
public class AccessControl {

    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final TopicRepo topicRepo;
    private final TaskRepo taskRepo;
    private final StudentProfileRepo studentProfileRepo;

    public AccessControl(UserRepo userRepo, CourseRepo courseRepo, TopicRepo topicRepo, TaskRepo taskRepo, StudentProfileRepo studentProfileRepo) {
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
        this.topicRepo = topicRepo;
        this.taskRepo = taskRepo;
        this.studentProfileRepo = studentProfileRepo;
    }

    public boolean isTeacherAssignedToCourse(Long courseId, String username) {
        try {
            Optional<User> userOpt = userRepo.findFirstByUsernameIgnoreCase(username);
            if (userOpt.isEmpty()) return false;
            User user = userOpt.get();
            Optional<Course> courseOpt = courseRepo.findById(courseId);
            if (courseOpt.isEmpty()) return false;
            Course course = courseOpt.get();
            if (course.getTeachers() == null) return false;
            return course.getTeachers().stream().anyMatch(t -> Objects.equals(t.getId(), user.getId()));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isStudentInCourse(Long courseId, String username) {
        try {
            Optional<User> userOpt = userRepo.findFirstByUsernameIgnoreCase(username);
            if (userOpt.isEmpty()) return false;
            User user = userOpt.get();
            Optional<StudentProfile> sp = studentProfileRepo.findByUserId(user.getId());
            if (sp.isEmpty() || sp.get().getGroup() == null) return false;
            Group g = sp.get().getGroup();
            if (g.getCourses() == null) return false;
            return g.getCourses().stream().anyMatch(c -> Objects.equals(c.getId(), courseId));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canCreateTopic(Long courseId, String username) {
        Optional<User> userOpt = userRepo.findFirstByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        if (user.getRole() == Role.ADMINISTRATOR) return true;
        if (user.getRole() == Role.TEACHER) return isTeacherAssignedToCourse(courseId, username);
        return false;
    }

    public boolean canManageTopic(Long topicId, String username) {
        try {
            Optional<User> userOpt = userRepo.findFirstByUsernameIgnoreCase(username);
            if (userOpt.isEmpty()) return false;
            User user = userOpt.get();
            if (user.getRole() == Role.ADMINISTRATOR) return true;

            Optional<Topic> topicOpt = topicRepo.findById(topicId);
            if (topicOpt.isEmpty()) return false;
            Topic topic = topicOpt.get();
            if (topic.getCourse() == null) return false;

            if (user.getRole() == Role.TEACHER) return isTeacherAssignedToCourse(topic.getCourse().getId(), username);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canCreateTask(Long topicId, String username) {
        try {
            Optional<User> userOpt = userRepo.findFirstByUsernameIgnoreCase(username);
            if (userOpt.isEmpty()) return false;
            User user = userOpt.get();
            if (user.getRole() == Role.ADMINISTRATOR) return true;

            Optional<Topic> topicOpt = topicRepo.findById(topicId);
            if (topicOpt.isEmpty()) return false;
            Topic topic = topicOpt.get();
            if (topic.getCourse() == null) return false;

            if (user.getRole() == Role.TEACHER) return isTeacherAssignedToCourse(topic.getCourse().getId(), username);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canManageTask(Long taskId, String username) {
        try {
            Optional<User> userOpt = userRepo.findFirstByUsernameIgnoreCase(username);
            if (userOpt.isEmpty()) return false;
            User user = userOpt.get();
            if (user.getRole() == Role.ADMINISTRATOR) return true;

            Optional<Task> taskOpt = taskRepo.findById(taskId);
            if (taskOpt.isEmpty()) return false;
            Task task = taskOpt.get();
            Topic topic = task.getTopic();
            if (topic == null || topic.getCourse() == null) return false;

            if (user.getRole() == Role.TEACHER) return isTeacherAssignedToCourse(topic.getCourse().getId(), username);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canViewTopic(Long topicId, String username) {
        try {
            Optional<User> userOpt = userRepo.findFirstByUsernameIgnoreCase(username);
            if (userOpt.isEmpty()) return false;
            User user = userOpt.get();
            if (user.getRole() == Role.ADMINISTRATOR) return true;

            Optional<Topic> topicOpt = topicRepo.findById(topicId);
            if (topicOpt.isEmpty()) return false;
            Topic topic = topicOpt.get();
            if (topic.getCourse() == null) return false;

            if (user.getRole() == Role.TEACHER) return isTeacherAssignedToCourse(topic.getCourse().getId(), username);
            if (user.getRole() == Role.STUDENT) return isStudentInCourse(topic.getCourse().getId(), username);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canViewTask(Long taskId, String username) {
        try {
            Optional<User> userOpt = userRepo.findFirstByUsernameIgnoreCase(username);
            if (userOpt.isEmpty()) return false;
            User user = userOpt.get();
            if (user.getRole() == Role.ADMINISTRATOR) return true;

            Optional<Task> taskOpt = taskRepo.findById(taskId);
            if (taskOpt.isEmpty()) return false;
            Task task = taskOpt.get();
            Topic topic = task.getTopic();
            if (topic == null || topic.getCourse() == null) return false;

            if (user.getRole() == Role.TEACHER) return isTeacherAssignedToCourse(topic.getCourse().getId(), username);
            if (user.getRole() == Role.STUDENT) return isStudentInCourse(topic.getCourse().getId(), username);
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canViewCourse(Long courseId, String username) {
        Optional<User> userOpt = userRepo.findFirstByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        if (user.getRole() == Role.ADMINISTRATOR) return true;
        if (user.getRole() == Role.TEACHER) return isTeacherAssignedToCourse(courseId, username);
        if (user.getRole() == Role.STUDENT) return isStudentInCourse(courseId, username);
        return false;
    }

    public boolean canSubmitTask(Long taskId, String username) {
        try {
            Optional<User> userOpt = userRepo.findFirstByUsernameIgnoreCase(username);
            if (userOpt.isEmpty()) return false;
            User user = userOpt.get();
            if (user.getRole() != Role.STUDENT) return false;
            Optional<StudentProfile> sp = studentProfileRepo.findByUserId(user.getId());
            if (sp.isEmpty() || sp.get().getGroup() == null) return false;
            Group g = sp.get().getGroup();
            Optional<Task> taskOpt = taskRepo.findById(taskId);
            if (taskOpt.isEmpty()) return false;
            Task task = taskOpt.get();
            Topic topic = task.getTopic();
            if (topic == null || topic.getCourse() == null) return false;
            Long courseId = topic.getCourse().getId();
            return g.getCourses().stream().anyMatch(c -> Objects.equals(c.getId(), courseId));
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isSelf(Long userId, String username) {
        try {
            return userRepo.findById(userId)
                    .map(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(username))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }
}
