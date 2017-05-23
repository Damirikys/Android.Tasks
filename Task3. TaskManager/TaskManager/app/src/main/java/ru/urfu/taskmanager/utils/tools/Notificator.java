package ru.urfu.taskmanager.utils.tools;

import android.app.NotificationManager;

public abstract class Notificator
{
    private Integer notificationID = 0;
    private NotificationManager notificationManager;

    protected abstract int getNotificationID();

    protected abstract NotificationManager bindNotificationManager();

    protected NotificationManager getNotificationManager() {
        return notificationManager;
    }
}
