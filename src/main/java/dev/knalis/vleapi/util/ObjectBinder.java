package dev.knalis.vleapi.util;

import dev.knalis.vleapi.model.entity.Course;
import dev.knalis.vleapi.model.entity.Group;
import dev.knalis.vleapi.model.entity.Topic;
import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.intrf.CourseService;
import dev.knalis.vleapi.service.intrf.GroupService;
import dev.knalis.vleapi.service.intrf.TopicService;
import dev.knalis.vleapi.service.intrf.UserService;
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

    @Transactional
    public void bindUserToGroup(Long userId, Long groupId) {
        User user = userService.findById(userId);
        Group group = groupService.findById(groupId);

        if (user.getGroup() != null) {
            throw new IllegalArgumentException("User is already part of a group");
        }

        group.addUser(user);

        userService.update(user);
        groupService.update(group);
    }

    @Transactional
    public void bindCourseToGroup(Long courseId, Long groupId) {
        Group group = groupService.findById(groupId);
        Course course = courseService.findById(courseId);

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
        Group group = groupService.findById(groupId);

        if (!group.getUsers().contains(user)) {
            throw new IllegalArgumentException("User is not part of the group");
        }
        group.getUsers().remove(user);
        user.setGroup(null);

        userService.update(user);
        groupService.update(group);
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

}
