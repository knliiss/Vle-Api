package dev.knalis.vleapi.util;

import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.model.entity.Topic;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.model.entity.user.Role;
import dev.knalis.vleapi.repo.StudentProfileRepo;
import dev.knalis.vleapi.model.entity.user.StudentProfile;
import dev.knalis.vleapi.service.intrf.CourseService;
import dev.knalis.vleapi.service.intrf.GroupService;
import dev.knalis.vleapi.service.intrf.TopicService;
import dev.knalis.vleapi.service.intrf.UserService;
import dev.knalis.vleapi.exception.custom.AlreadyBoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ObjectBinder {

    private final UserService userService;
    private final GroupService groupService;
    private final CourseService courseService;
    private final TopicService topicService;
    private final StudentProfileRepo studentProfileRepo;

    @Transactional
    public void bindUserToGroup(Long userId, Long groupId) {
        User user = userService.findById(userId);
        Group group = groupService.findById(groupId);
        if (user.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Only STUDENT users can be bound to a group");
        }
        StudentProfile sp = studentProfileRepo.findByUserId(user.getId()).orElseGet(() -> {
            StudentProfile created = new StudentProfile();
            created.setUser(user);
            return studentProfileRepo.save(created);
        });
        if (sp.getGroup() != null && sp.getGroup().getId().equals(groupId)) {
            return;
        }
        sp.setGroup(group);
        studentProfileRepo.save(sp);
    }

    @Transactional
    public void bindCourseToGroup(Long courseId, Long groupId) {
        Group group = groupService.findById(groupId);
        Course course = courseService.findById(courseId);
        if (group.getCourses().contains(course)) {
            throw new AlreadyBoundException("Group already bound to course");
        }
        group.addCourse(course);
        courseService.update(course);
        groupService.update(group);
    }

    @Transactional
    public void bindTopicToCourse(Long topicId, Long courseId) {
        Course course = courseService.findById(courseId);
        Topic topic = topicService.findById(topicId);

        course.addTopic(topic);

        topicService.update(topic);
        courseService.update(course);
    }

    @Transactional
    public void unbindUserFromGroup(Long userId, Long groupId) {
        User user = userService.findById(userId);
        if (user.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Only STUDENT users can be unbound from a group");
        }
        StudentProfile sp = studentProfileRepo.findByUserId(user.getId()).orElseThrow(() -> new IllegalArgumentException("Student profile not found"));
        if (sp.getGroup()==null || !sp.getGroup().getId().equals(groupId)) {
            throw new IllegalArgumentException("Student not in specified group");
        }
        sp.setGroup(null);
        studentProfileRepo.save(sp);
    }

    @Transactional
    public void unbindCourseFromGroup(Long courseId, Long groupId) {
        Group group = groupService.findById(groupId);
        Course course = courseService.findById(courseId);

        if (!group.getCourses().contains(course)) {
            throw new IllegalArgumentException("Course is not part of the group");
        }
        group.getCourses().remove(course);

        courseService.update(course);
        groupService.update(group);
    }

    @Transactional
    public void unbindTopicFromCourse(Long topicId, Long courseId) {
        Course course = courseService.findById(courseId);
        Topic topic = topicService.findById(topicId);
        if (!course.getTopics().contains(topic)) {
            throw new IllegalArgumentException("Topic is not part of the course");
        }
        course.getTopics().remove(topic);
        topicService.update(topic);
        courseService.update(course);
    }

    @Transactional
    public void bindTeacherToCourse(Long teacherId, Long courseId) {
        User teacher = userService.findById(teacherId);
        if (teacher.getRole() != Role.TEACHER && teacher.getRole() != Role.ADMINISTRATOR) {
            throw new IllegalArgumentException("User must be TEACHER or ADMINISTRATOR to be assigned as course teacher");
        }
        Course course = courseService.findById(courseId);
        if (course.getTeachers().contains(teacher)) {
            throw new IllegalArgumentException("Teacher already assigned to course");
        }
        course.addTeacher(teacher);
        courseService.update(course);
    }

    @Transactional
    public void unbindTeacherFromCourse(Long teacherId, Long courseId) {
        User teacher = userService.findById(teacherId);
        Course course = courseService.findById(courseId);
        if (!course.getTeachers().contains(teacher)) {
            throw new IllegalArgumentException("Teacher is not assigned to course");
        }
        course.removeTeacher(teacher);
        courseService.update(course);
    }

}
