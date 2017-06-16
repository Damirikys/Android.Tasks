package ru.urfu.taskmanager.interfaces;

public interface Callback<T>
{
    void call(T obj);
}
