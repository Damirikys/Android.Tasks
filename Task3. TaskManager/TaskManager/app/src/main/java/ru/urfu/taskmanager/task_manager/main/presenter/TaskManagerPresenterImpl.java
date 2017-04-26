package ru.urfu.taskmanager.task_manager.main.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.urfu.taskmanager.Application;
import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListView;
import ru.urfu.taskmanager.task_manager.main.view.TaskManager;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.utils.interfaces.Callback;
import ru.urfu.taskmanager.utils.interfaces.Coupler;

import static android.app.Activity.RESULT_OK;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_CREATE;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_EDIT;

public class TaskManagerPresenterImpl implements TaskManagerPresenter
{
    private final TaskManager view;
    private final TasksDatabase database;
    private final List<TaskListView> taskLists;

    public TaskManagerPresenterImpl(TaskManager view) {
        this.view = view;
        this.taskLists = new ArrayList<>();
        this.database = TasksDatabase.getInstance();
    }

    @Override
    public void taskIsCompleted(int id) {
        database.updateEntry(
                database.getEntryById(id)
                        .setTtl(System.currentTimeMillis())
                        .setCompleted(true)
        );

        notifyDataUpdate();
    }

    @Override
    public void postponeTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler) {
        TaskEntry task = database.getEntryById(id);
        coupler.bind(date -> {
            task.setTtl(date.getTime());
            database.updateEntry(task);
            notifyDataUpdate();
        }, task);
    }

    @Override
    public void deleteTheTask(int id) {
        database.removeEntryById(id);
        notifyDataUpdate();
    }

    @Override
    public void restoreTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler) {
        TaskEntry task = database.getEntryById(id)
                .setCompleted(false);

        coupler.bind(date -> {
            task.setTtl(date.getTime());
            database.updateEntry(task);
            notifyDataUpdate();
        }, task);
    }

    @Override
    public void editTheTask(int id) {
        view.startEditor(id);
    }

    @Override
    public TaskListView bindView(TaskListView view) {
        taskLists.add(view);
        return view.bindPresenter(this);
    }

    @Override
    public void onResult(int requestCode, int resultCode) {
        if (resultCode == RESULT_OK) {
            notifyDataUpdate();

            switch (requestCode) {
                case REQUEST_CREATE:
                    view.showAlert(view.getResources().getString(R.string.task_was_created));
                    break;
                case REQUEST_EDIT:
                    view.showAlert(view.getResources().getString(R.string.task_was_updated));
                    break;
            }
        }
    }

    private void notifyDataUpdate() {
        for (TaskListView taskList : taskLists) {
            taskList.onUpdate();
        }
    }
}
