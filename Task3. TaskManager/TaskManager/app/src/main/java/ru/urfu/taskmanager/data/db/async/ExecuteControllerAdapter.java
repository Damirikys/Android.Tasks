package ru.urfu.taskmanager.data.db.async;

import android.app.NotificationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.urfu.taskmanager.utils.tools.Notificator;

public abstract class ExecuteControllerAdapter<T> extends Notificator implements ExecuteController<T>
{
    private NotificationManager notificationManager;

    @Override
    public void onStart() {
        // Stub!
    }

    @Override
    public void onProgress(int value) {
        // Stub!
    }

    @Override
    public final void onFinish(@Nullable T result) {
        if (result != null) {
            onResult(result);
        } else {
            onFailed();
        }

        onFinish();
    }

    public void onFinish() {
        //Stub
    }

    public void onResult(@NonNull T result) {
        // Stub !
    }

    public void onFailed() {
        //Stub
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
        return (notificationManager == null)
                ? notificationManager = bindNotificationManager()
                : notificationManager;
    }
}
