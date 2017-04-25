package ru.urfu.taskmanager.utils.interfaces;

public interface Thenable<T, V> {
    void onSuccess(T t);
    void onFailed(V t);
}
