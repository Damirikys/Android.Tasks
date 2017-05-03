package ru.urfu.taskmanager.task_manager.fragments.view;

import android.support.v4.app.Fragment;

import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenter;
import ru.urfu.taskmanager.task_manager.models.OnDataUpdateListener;
import ru.urfu.taskmanager.utils.db.TasksFilter;
import ru.urfu.taskmanager.utils.interfaces.Showable;

public interface TaskListView extends OnDataUpdateListener<TasksFilter.Builder>, Showable {
    TaskListView bindPresenter(TaskManagerPresenter presenter);

    Fragment getInstance();
}
