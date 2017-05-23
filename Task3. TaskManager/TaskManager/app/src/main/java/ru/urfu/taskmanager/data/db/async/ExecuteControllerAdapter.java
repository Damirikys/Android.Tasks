package ru.urfu.taskmanager.data.db.async;

import android.app.NotificationManager;
import android.support.annotation.Nullable;

import ru.urfu.taskmanager.utils.tools.Notificator;

public abstract class ExecuteControllerAdapter<T> extends Notificator implements ExecuteController<T>
{
    @Override
    public void onStart() {
        // Stub!
    }

    @Override
    public void onProgress(int value) {
        // Stub!
    }

    @Override
    public void onFinish(@Nullable T result) {
        onFinish();
    }

    public void onFinish() {
        // Stub !
    }

    @Override
    protected int getNotificationID() {
        return 0;
    }

    @Override
    protected NotificationManager bindNotificationManager() {
        return null;
    }

    @Override
    protected NotificationManager getNotificationManager() {
        return (super.getNotificationManager() == null) ?
                bindNotificationManager() : super.getNotificationManager();
    }
}
