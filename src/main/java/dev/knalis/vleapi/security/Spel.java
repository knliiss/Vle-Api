package dev.knalis.vleapi.security;

public final class Spel {
    private Spel() {}

    public static final String HAS_ADMIN   = "hasRole('" + Roles.ADMIN + "')";
    public static final String HAS_TEACHER = "hasRole('" + Roles.TEACHER + "')";
    public static final String HAS_STUDENT = "hasRole('" + Roles.STUDENT + "')";

    public static final String CAN_VIEW_COURSE   = "@accessControl.canViewCourse(#id, principal.username)";
    public static final String CAN_CREATE_TOPIC  = "@accessControl.canCreateTopic(#request.courseId, principal.username)";
    public static final String CAN_MANAGE_TOPIC  = "@accessControl.canManageTopic(#id, principal.username)";
    public static final String CAN_VIEW_TOPIC    = "@accessControl.canViewTopic(#id, principal.username)";
    public static final String CAN_CREATE_TASK   = "@accessControl.canCreateTask(#request.topicId, principal.username)";
    public static final String CAN_MANAGE_TASK   = "@accessControl.canManageTask(#id, principal.username)";
    public static final String CAN_VIEW_TASK     = "@accessControl.canViewTask(#id, principal.username)";

    public static final String IS_SELF_BY_PATH_ID       = "isAuthenticated() and principal.username == @userService.findById(#id).username";
    public static final String IS_SELF_BY_USERID_PARAM  = "isAuthenticated() and principal.username == @userService.findById(#userId).username";
}
