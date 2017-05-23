package ru.urfu.taskmanager.utils.interfaces;

public interface Thenable<T>
{
    void onSuccess(T... results);
    void onFailed(Throwable t);
}
