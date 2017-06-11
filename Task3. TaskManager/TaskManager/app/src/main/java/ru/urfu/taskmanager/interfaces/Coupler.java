package ru.urfu.taskmanager.interfaces;

public interface Coupler<T, V>
{
    void bind(T first, V second);
}
