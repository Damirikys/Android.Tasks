package ru.urfu.taskmanager.task_manager.main.adapters;

import ru.urfu.taskmanager.data.db.DbFilter;

public interface OnDataUpdateListener<T>
{
    void onUpdate(T... data);

    void onUpdate(DbFilter filter);
}
