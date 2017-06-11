package ru.urfu.taskmanager.db;

import android.database.Cursor;

import java.util.List;

import ru.urfu.taskmanager.db.filter.DbFilter;
import ru.urfu.taskmanager.interfaces.Callback;

@SuppressWarnings("unused")
public interface SimpleDatabase<T>
{
    List<T> getAllEntries();

    T getEntryById(int id);

    long insertEntry(T entry);

    void removeEntryById(int id);

    void replaceAll(List<T> entries);

    T updateEntry(T entry);

    void startTransaction(Callback<Void> callback);

    T getCurrentEntryFromCursor(Cursor cursor);

    Cursor getCursor(DbFilter filter);
}
