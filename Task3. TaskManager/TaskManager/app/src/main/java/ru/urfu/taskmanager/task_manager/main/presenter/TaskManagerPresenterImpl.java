package ru.urfu.taskmanager.task_manager.main.presenter;

import android.content.Intent;
import android.net.Uri;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ru.urfu.taskmanager.Application;
import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListView;
import ru.urfu.taskmanager.task_manager.main.view.TaskManager;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.utils.db.TasksFilter;
import ru.urfu.taskmanager.utils.interfaces.Callback;
import ru.urfu.taskmanager.utils.interfaces.Coupler;
import ru.urfu.taskmanager.utils.tools.JSONFactory;

import static android.app.Activity.RESULT_OK;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_CREATE;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_EDIT;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_IMPORT;

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
    public void applyFilter(TasksFilter.Builder filter) {
        notifyDataUpdate(filter);
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CREATE:
                    view.showAlert(view.getResources().getString(R.string.task_was_created));
                    break;
                case REQUEST_EDIT:
                    view.showAlert(view.getResources().getString(R.string.task_was_updated));
                    break;
                case REQUEST_IMPORT:
                    importFrom(data.getData());
                    break;
            }

            notifyDataUpdate();
        }
    }

    private void importFrom(Uri uri) {
        StringBuilder builder = new StringBuilder();
        try {
            InputStream inputStream = Application.getContext()
                    .getContentResolver()
                    .openInputStream(uri);

            if (inputStream == null)
                throw new FileNotFoundException();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(inputStream)
            );

            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }

            br.close();

            List<TaskEntry> entries = JSONFactory.fromJson(builder.toString(),
                    Types.newParameterizedType(List.class, TaskEntry.class));

            database.replaceAll(entries);
            view.showAlert("Задачи успешно импортированы");
        }
        catch (IOException e) {
            e.printStackTrace();
            view.showAlert("Не удалось импортировать задачи");
        }
    }

    private void notifyDataUpdate() {
        notifyDataUpdate(TasksFilter.DEFAULT_BUILDER);
    }

    private void notifyDataUpdate(TasksFilter.Builder builder) {
        for (TaskListView taskList : taskLists) {
            taskList.onUpdate(builder.copy());
        }
    }
}
