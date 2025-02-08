package com.apigenerator.intellij.notifications;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;

import static com.intellij.lang.annotation.HighlightSeverity.*;

public class NotificationHelper {

    private static final String GROUP_ID = "API Generator";

    public static void notifyInfo(Project project, String title, String content) {
        notify(project, title, content, INFORMATION);
    }

    private static void notify(Project project, String title, String content, HighlightSeverity information) {
    }

    public static void notifyError(Project project, String title, String content) {
        notify(project, title, content, ERROR);
    }

    public static void notifyWarning(Project project, String title, String content) {
        notify(project, title, content, WARNING);
    }

//    private static void notify(Project project, String title, String content, NotificationType type) {
//        Notification notification = new Notification(GROUP_ID, title, content, type);
//        Notifications.Bus.notify(notification, project);
//    }
}