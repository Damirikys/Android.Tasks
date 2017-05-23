package ru.urfu.taskmanager.task_manager.main.presenter;

import android.content.Intent;
import android.net.Uri;

import java.util.Date;

import ru.urfu.taskmanager.task_manager.fragments.view.TaskListView;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.db.DbTasksFilter;
import ru.urfu.taskmanager.utils.interfaces.Callback;
import ru.urfu.taskmanager.utils.interfaces.Coupler;

public interface TaskManagerPresenter
{
    TaskListView bindView(TaskListView view);

    void taskIsCompleted(int id);

    void postponeTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler);

    void deleteTheTask(int id);

    void restoreTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler);

    void editTheTask(int id);

    void applyFilter(DbTasksFilter.Builder filterBuilder);

    void generateBigData();

    void exportData(String path);

    void onResult(int requestCode, int resultCode, Intent data);

    void onDestroy();
}
