package ru.urfu.taskmanager.task_manager.fragments.view;

import android.support.v4.app.Fragment;

import ru.urfu.taskmanager.task_manager.fragments.presenter.TaskListPresenter;
import ru.urfu.taskmanager.task_manager.models.TaskDataListener;
import ru.urfu.taskmanager.utils.interfaces.Showable;

public interface TaskListView extends TaskDataListener, Showable {
    TaskListPresenter getPresenter();
    Fragment getInstance();
}
