package ru.urfu.taskmanager.task_manager.main.view;

import android.view.View;

import ru.urfu.taskmanager.utils.interfaces.Resource;
import ru.urfu.taskmanager.utils.interfaces.Showable;

public interface TaskManager extends View.OnClickListener, Showable, Resource {
    void startEditor(int id);
}
