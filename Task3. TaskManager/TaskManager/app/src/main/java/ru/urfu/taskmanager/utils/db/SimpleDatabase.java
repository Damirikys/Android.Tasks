package ru.urfu.taskmanager.utils.db;

import android.database.Cursor;

import java.util.List;

import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.interfaces.Callback;

public interface SimpleDatabase<T>
{
    List<T> getAllEntries();

    T getEntryById(int id);

    void insertEntry(T entry);

    void removeEntryById(int id);

    void replaceAll(List<T> entries);

    T updateEntry(T entry);

    void startTransaction(Callback<Void> callback);

    T getCurrentEntryFromCursor(Cursor cursor);

    Cursor getCursor(DbFilter filter);
}
