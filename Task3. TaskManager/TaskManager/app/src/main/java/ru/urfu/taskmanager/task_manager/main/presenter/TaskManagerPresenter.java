package ru.urfu.taskmanager.task_manager.main.presenter;

import ru.urfu.taskmanager.task_manager.models.TaskDataListener;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListView;

public interface TaskManagerPresenter extends TaskDataListener
{
    TaskListView addModel(TaskListView model);
    void onResult(int requestCode, int resultCode);
    void onDestroy();
}
