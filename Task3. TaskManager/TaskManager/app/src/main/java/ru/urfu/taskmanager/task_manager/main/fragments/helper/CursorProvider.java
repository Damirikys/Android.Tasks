package ru.urfu.taskmanager.task_manager.main.fragments.helper;

import android.database.Cursor;

import ru.urfu.taskmanager.task_manager.main.fragments.helper.ItemTouchHelperAdapter;

public interface CursorProvider extends ItemTouchHelperAdapter
{
    Cursor getCursor();
}
