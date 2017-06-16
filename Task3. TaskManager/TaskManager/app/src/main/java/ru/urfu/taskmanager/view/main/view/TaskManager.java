package ru.urfu.taskmanager.view.main.view;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import ru.urfu.taskmanager.view.main.fragments.helper.CursorProvider;
import ru.urfu.taskmanager.view.main.fragments.adapters.TasksListAdapter;
import ru.urfu.taskmanager.view.main.filter.FilterLayoutWrapper;
import ru.urfu.taskmanager.view.main.presenter.TaskManagerPresenter;
import ru.urfu.taskmanager.interfaces.Progressive;

public interface TaskManager extends Progressive
{
    TaskManagerPresenter getPresenter();

    FilterLayoutWrapper getFilterLayoutWrapper();

    FragmentManager getSupportFragmentManager();

    void syncData();

    void startEditor(@Nullable Integer position, CursorProvider adapter, TasksListAdapter.ViewHolder viewHolder);
}
