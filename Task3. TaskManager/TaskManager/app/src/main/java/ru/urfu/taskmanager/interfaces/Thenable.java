package ru.urfu.taskmanager.interfaces;

@SuppressWarnings({"unchecked", "unused"})
public interface Thenable<T>
{
    void onSuccess(T... results);

    void onFailed(Throwable t);
}
