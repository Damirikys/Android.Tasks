package ru.urfu.taskmanager.task_manager.main.presenter;

import java.util.Date;

import ru.urfu.taskmanager.task_manager.fragments.view.TaskListView;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.interfaces.Callback;
import ru.urfu.taskmanager.utils.interfaces.Coupler;

public interface TaskManagerPresenter
{
    TaskListView bindView(TaskListView view);
    void taskIsCompleted(int id);
    void postponeTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler);
    void deleteTheTask(int id);
    void restoreTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler);
    void onResult(int requestCode, int resultCode);
}
