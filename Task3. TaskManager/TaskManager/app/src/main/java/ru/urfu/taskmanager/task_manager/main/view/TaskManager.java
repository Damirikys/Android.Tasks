package ru.urfu.taskmanager.task_manager.main.view;

import android.view.View;

import ru.urfu.taskmanager.utils.interfaces.ActivityWindow;
import ru.urfu.taskmanager.utils.interfaces.Showable;

public interface TaskManager extends View.OnClickListener, Showable, ActivityWindow {
    void startEditor(int id);
}
