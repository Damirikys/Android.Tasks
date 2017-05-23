package ru.urfu.taskmanager.task_manager.main.view;

import android.view.View;

import ru.urfu.taskmanager.utils.interfaces.Progressive;

public interface TaskManager extends View.OnClickListener, Progressive
{
    void startEditor(int id);
}
