package ru.urfu.taskmanager.task_manager.fragments.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.utils.db.TasksDatabaseHelper;


public class AbstractTaskListAdapter extends SimpleCursorAdapter
{
    protected static final String OVERDUE = "Просрочено";

    protected static final int LAYOUT = R.layout.task_list_item;

    protected static final String[] FROM = new String[]{
            TasksDatabaseHelper.TITLE,
            TasksDatabaseHelper.DESCRIPTION,
            TasksDatabaseHelper.TTL,
            TasksDatabaseHelper.DECORATE_COLOR,
    };

    protected static final int[] TO = new int[]{
            R.id.task_item_title,
            R.id.task_item_description,
            R.id.task_item_deadline,
            R.id.task_item_color
    };

    protected static final String[] ACTIVE_DAYS = new String[]{
            "Сегодня", "Завтра", "Послезавтра"
    };

    protected static final String[] COMPLETED_DAYS = new String[]{
            "Сегодня", "Вчера", "Позавчера"
    };

    protected AbstractTaskListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }
}
