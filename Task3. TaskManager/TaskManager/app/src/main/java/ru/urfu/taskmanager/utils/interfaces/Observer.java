package ru.urfu.taskmanager.utils.interfaces;

public interface Observer<T, V>
{
    void subscribe(T obj);
    void unsubscribe(T obj);
    void notifySubscribers(V changes);
}
