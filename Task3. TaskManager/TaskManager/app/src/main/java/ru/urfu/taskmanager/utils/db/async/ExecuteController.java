package ru.urfu.taskmanager.utils.db.async;

import android.support.annotation.Nullable;

public interface ExecuteController<T>
{
    void onStart();

    void onProgress(int value);

    void onFinish(@Nullable T result);
}
