package ru.urfu.taskmanager.utils.tools;

import android.app.NotificationManager;

@SuppressWarnings("unused")
public abstract class Notificator
{
    protected abstract int getNotificationID();

    protected abstract NotificationManager bindNotificationManager();

    protected abstract NotificationManager getNotificationManager();
}
