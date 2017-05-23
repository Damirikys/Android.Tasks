package ru.urfu.taskmanager.task_manager.main.view;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import ru.urfu.taskmanager.task_manager.main.fragments.helper.CursorProvider;
import ru.urfu.taskmanager.task_manager.main.fragments.adapters.TasksListAdapter;
import ru.urfu.taskmanager.task_manager.main.filter.FilterLayoutWrapper;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenter;
import ru.urfu.taskmanager.utils.interfaces.Progressive;

public interface TaskManager extends Progressive
{
    TaskManagerPresenter getPresenter();

    FilterLayoutWrapper getFilterLayoutWrapper();

    FragmentManager getSupportFragmentManager();

    void syncData();

    void startEditor(@Nullable Integer position, CursorProvider adapter, TasksListAdapter.ViewHolder viewHolder);
}
