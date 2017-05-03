package ru.urfu.taskmanager.utils.interfaces;

public interface Coupler<T, V>
{
    void bind(T first, V second);
}
