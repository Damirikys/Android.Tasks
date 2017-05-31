package ru.urfu.taskmanager.task_manager.main.fragments.helper;

import android.database.Cursor;

public interface CursorProvider extends ItemTouchHelperAdapter
{
    Cursor getCursor();
}
