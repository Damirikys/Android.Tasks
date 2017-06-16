package ru.urfu.taskmanager.view.main.fragments.view;

import android.database.Cursor;

import ru.urfu.taskmanager.view.main.adapters.OnDataUpdateListener;
import ru.urfu.taskmanager.interfaces.Showable;

public interface TaskListView extends OnDataUpdateListener<Cursor>, Showable
{
    int getDataType();

}
