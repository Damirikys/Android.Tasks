package ru.urfu.taskmanager.task_manager.models;

public interface OnDataUpdateListener<T>
{
    void onUpdate(T... data);
}
