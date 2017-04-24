package ru.urfu.taskmanager.task_manager.main.presenter;

import java.util.ArrayList;
import java.util.List;

import ru.urfu.taskmanager.Application;
import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.fragments.presenter.TaskListPresenter;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListView;
import ru.urfu.taskmanager.task_manager.main.view.TaskManager;

import static android.app.Activity.RESULT_OK;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_CREATE;

public class TaskManagerPresenterImpl implements TaskManagerPresenter
{
    private final TaskManager view;
    private final List<TaskListPresenter> observers;

    public TaskManagerPresenterImpl(TaskManager view) {
        this.view = view;
        this.observers = new ArrayList<>();
    }

    @Override
    public TaskListView addModel(TaskListView model) {
        TaskListPresenter modelPresenter = model.getPresenter();
        modelPresenter.subscribe(this);
        observers.add(modelPresenter);
        return model;
    }

    @Override
    public void onResult(int requestCode, int resultCode) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CREATE) {
                view.showAlert(Application.getContext().getString(R.string.task_was_created));
                onUpdate();
            }
        }
    }

    @Override
    public void onUpdate() {
        for (TaskListPresenter observer : observers) {
            observer.onUpdate();
        }
    }

    @Override
    public void onDestroy() {
        for (TaskListPresenter observer : observers) {
            observer.unsubscribe(this);
        }
    }
}
