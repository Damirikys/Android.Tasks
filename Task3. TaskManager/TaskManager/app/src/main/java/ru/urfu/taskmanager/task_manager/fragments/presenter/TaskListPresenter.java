package ru.urfu.taskmanager.task_manager.fragments.presenter;

import java.util.Date;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.task_manager.models.TaskDataListener;
import ru.urfu.taskmanager.utils.interfaces.Callback;
import ru.urfu.taskmanager.utils.interfaces.Coupler;
import ru.urfu.taskmanager.utils.interfaces.Observer;

public interface TaskListPresenter extends Observer<TaskDataListener, Void>, TaskDataListener
{
    void taskIsCompleted(int id);
    void postponeTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler);
    void deleteTheTask(int id);
    void restoreTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler);
    void onResult(int requestCode, int resultCode);
}
