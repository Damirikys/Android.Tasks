package ru.urfu.taskmanager.view.main.adapters;

import ru.urfu.taskmanager.db.filter.DbFilter;

@SuppressWarnings("unchecked")
public interface OnDataUpdateListener<T>
{
    void onUpdate(T... data);

    void onUpdate(DbFilter filter);
}
