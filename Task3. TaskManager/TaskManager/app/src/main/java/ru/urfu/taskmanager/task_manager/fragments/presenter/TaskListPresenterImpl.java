package ru.urfu.taskmanager.task_manager.fragments.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListView;
import ru.urfu.taskmanager.task_manager.models.TaskDataListener;
import ru.urfu.taskmanager.utils.interfaces.Callback;
import ru.urfu.taskmanager.utils.interfaces.Coupler;

import static android.app.Activity.RESULT_OK;
import static ru.urfu.taskmanager.Application.getContext;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_EDIT;

public class TaskListPresenterImpl implements TaskListPresenter
{
    private TaskListView model;
    private TasksDatabase database;
    private List<TaskDataListener> listeners;

    public TaskListPresenterImpl(TaskListView model) {
        this.model = model;
        this.database = TasksDatabase.getInstance();
        this.listeners = new ArrayList<>();
    }

    @Override
    public void taskIsCompleted(int id) {
        database.updateEntry(
                database.getEntryById(id)
                        .setCompleted(true)
        );

        notifySubscribers(null);
    }

    @Override
    public void postponeTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler) {
        TaskEntry task = database.getEntryById(id);
        coupler.bind(date -> {
            task.setTtl(date.getTime());
            database.updateEntry(task);
            notifySubscribers(null);
        }, task);
    }

    @Override
    public void deleteTheTask(int id) {
        database.removeEntryById(id);
        notifySubscribers(null);
    }

    @Override
    public void restoreTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler) {
        TaskEntry task = database.getEntryById(id)
                .setCompleted(false);

        coupler.bind(date -> {
            task.setTtl(date.getTime());
            database.updateEntry(task);
            notifySubscribers(null);
        }, task);
    }

    @Override
    public void onResult(int requestCode, int resultCode) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_EDIT) {
                notifySubscribers(null);
                model.showAlert(getContext().getString(R.string.task_was_updated));
            }
        }
    }

    @Override
    public void subscribe(TaskDataListener obj) {
        listeners.add(obj);
    }

    @Override
    public void unsubscribe(TaskDataListener obj) {
        listeners.remove(obj);
    }

    @Override
    public void notifySubscribers(Void changes) {
        for (TaskDataListener listener : listeners) {
            listener.onUpdate();
        }
    }

    @Override
    public void onUpdate() {
        model.onUpdate();
    }
}
