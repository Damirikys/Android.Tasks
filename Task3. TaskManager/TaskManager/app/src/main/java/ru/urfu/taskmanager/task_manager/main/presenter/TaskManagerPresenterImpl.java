package ru.urfu.taskmanager.task_manager.main.presenter;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListView;
import ru.urfu.taskmanager.task_manager.main.tools.DataExportController;
import ru.urfu.taskmanager.task_manager.main.tools.DataImportController;
import ru.urfu.taskmanager.task_manager.main.tools.BackupManager;
import ru.urfu.taskmanager.task_manager.main.view.TaskManager;
import ru.urfu.taskmanager.task_manager.main.tools.TasksGenerator;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.db.async.DbAsyncExecutor;
import ru.urfu.taskmanager.utils.db.DbFilter;
import ru.urfu.taskmanager.utils.db.DbTasks;
import ru.urfu.taskmanager.utils.db.DbTasksFilter;
import ru.urfu.taskmanager.utils.db.async.ExecuteControllerAdapter;
import ru.urfu.taskmanager.utils.interfaces.Callback;
import ru.urfu.taskmanager.utils.interfaces.Coupler;

import static android.app.Activity.RESULT_OK;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_CREATE;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_EDIT;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_IMPORT;

public class TaskManagerPresenterImpl implements TaskManagerPresenter
{
    private static final String EXPORTED_FILENAME = "itemlist.ili";

    private final TaskManager mManager;
    private final DbAsyncExecutor<TaskEntry> dbAsyncExecutor;
    private final BackupManager mDataExporter;
    private final List<TaskListView> mTasksList;

    public TaskManagerPresenterImpl(TaskManager view) {
        this.mManager = view;
        this.mTasksList = new ArrayList<>();
        this.dbAsyncExecutor = DbTasks.getInstance().getAsyncExecutor();
        this.mDataExporter = new BackupManager();
    }

    @Override
    public void taskIsCompleted(int id) {
        TaskEntry updatedEntry = new TaskEntry(id)
                .setTtl(System.currentTimeMillis())
                .setCompleted(true);

        dbAsyncExecutor.updateEntry(updatedEntry, new ExecuteControllerAdapter<TaskEntry>() {
            @Override
            public void onFinish() {
                notifyDataUpdate();
            }
        });
    }

    @Override
    public void postponeTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler) {
        dbAsyncExecutor.getEntryById(id, new ExecuteControllerAdapter<TaskEntry>() {
            @Override
            public void onFinish(TaskEntry entry) {
                coupler.bind(date -> {
                    entry.setTtl(date.getTime());
                    dbAsyncExecutor.updateEntry(entry, new ExecuteControllerAdapter<TaskEntry>() {
                        @Override
                        public void onFinish() {
                            notifyDataUpdate();
                        }
                    });
                }, entry);
            }
        });
    }

    @Override
    public void deleteTheTask(int id) {
        dbAsyncExecutor.removeEntryById(id, new ExecuteControllerAdapter<Void>() {
            @Override
            public void onFinish() {
                notifyDataUpdate();
            }
        });
    }

    @Override
    public void restoreTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler) {
        dbAsyncExecutor.getEntryById(id, new ExecuteControllerAdapter<TaskEntry>() {
            @Override
            public void onFinish(TaskEntry entry) {
                entry.setCompleted(false);
                coupler.bind(date -> {
                    entry.setTtl(date.getTime());
                    dbAsyncExecutor.updateEntry(entry, new ExecuteControllerAdapter<TaskEntry>() {
                        @Override
                        public void onFinish() {
                            notifyDataUpdate();
                        }
                    });
                }, entry);
            }
        });
    }

    @Override
    public void editTheTask(int id) {
        mManager.startEditor(id);
    }

    @Override
    public void applyFilter(DbTasksFilter.Builder filter) {
        notifyDataUpdate(filter);
    }

    @Override
    public void generateBigData() {
        new TasksGenerator(this, mManager).generate();
    }

    @Override
    public void exportData(String path) {
        dbAsyncExecutor.getAllEntries(
                new DataExportController<>(
                        mManager.getBaseContext(), mManager,
                        path, EXPORTED_FILENAME,
                        mDataExporter
                )
        );
    }

    private void importData(Uri uri) throws FileNotFoundException {
        InputStream inputStream = mManager.getBaseContext()
                .getContentResolver()
                .openInputStream(uri);

        mDataExporter.importFrom(inputStream, TaskEntry.class,
                new DataImportController<>(mManager, dbAsyncExecutor, aVoid -> notifyDataUpdate())
        );
    }

    @Override
    public TaskListView bindView(TaskListView view) {
        mTasksList.add(view);
        return view.bindPresenter(this);
    }

    @Override
    public void onResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CREATE:
                    mManager.showAlert(mManager.getResources().getString(R.string.task_was_created));
                    break;
                case REQUEST_EDIT:
                    mManager.showAlert(mManager.getResources().getString(R.string.task_was_updated));
                    break;
                case REQUEST_IMPORT:
                    try {
                        importData(data.getData());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }

            notifyDataUpdate();
        }
    }

    private void notifyDataUpdate() {
        notifyDataUpdate(DbTasksFilter.DEFAULT_BUILDER);
    }

    private void notifyDataUpdate(DbTasksFilter.Builder builder) {
        if (builder.isDefault()) {
            sendAction(taskList -> taskList.onUpdate());
        } else {
            AtomicInteger counter = new AtomicInteger(0);

            sendAction(taskList -> {
                DbFilter filter = builder.copy()
                        .setType(taskList.getDataType())
                        .build();

                dbAsyncExecutor.getCursor(filter, new ExecuteControllerAdapter<Cursor>() {
                        @Override
                        public void onStart() {
                            mManager.showProgress();
                            counter.incrementAndGet();
                        }

                        @Override
                        public void onFinish(Cursor cursor) {
                            taskList.onUpdate(cursor);
                            if (counter.decrementAndGet() == 0)
                                mManager.hideProgress();
                        }
                    }
                );
            });
        }
    }

    private void sendAction(Callback<TaskListView> callback) {
        for (TaskListView taskList : mTasksList)
            callback.call(taskList);
    }


    @Override
    public void onDestroy() {
        mDataExporter.quit();
    }
}
