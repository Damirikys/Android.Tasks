package ru.urfu.taskmanager.view.main.fragments.helper;

import android.database.Cursor;

public interface CursorProvider extends ItemTouchHelperAdapter
{
    Cursor getCursor();
}
