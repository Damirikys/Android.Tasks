package ru.urfu.taskmanager.task_manager.main.presenter;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.auth.models.User;
import ru.urfu.taskmanager.data.backup.BackupManager;
import ru.urfu.taskmanager.data.backup.DataExportController;
import ru.urfu.taskmanager.data.backup.DataImportController;
import ru.urfu.taskmanager.data.backup.TasksGenerator;
import ru.urfu.taskmanager.data.db.DbFilter;
import ru.urfu.taskmanager.data.db.DbTasks;
import ru.urfu.taskmanager.data.db.DbTasksFilter;
import ru.urfu.taskmanager.data.db.async.DbAsyncExecutor;
import ru.urfu.taskmanager.data.db.async.ExecuteControllerAdapter;
import ru.urfu.taskmanager.data.network.APIServiceExecutor;
import ru.urfu.taskmanager.task_manager.main.fragments.helper.CursorProvider;
import ru.urfu.taskmanager.task_manager.main.fragments.adapters.TasksListAdapter;
import ru.urfu.taskmanager.task_manager.main.fragments.view.TaskListView;
import ru.urfu.taskmanager.task_manager.main.view.TaskManager;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.interfaces.Callback;
import ru.urfu.taskmanager.utils.interfaces.Coupler;

import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_CREATE;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_EDIT;

public class TaskManagerPresenterImpl implements TaskManagerPresenter
{
    private static final String EXPORTED_FILENAME = "itemlist.ili";

    private final TaskManager mManager;
    private final APIServiceExecutor mAPIServiceExecutor;
    private final DbAsyncExecutor<TaskEntry> mDbAsyncExecutor;
    private final BackupManager mDataExporter;
    private final List<TaskListView> mTasksList;

    public TaskManagerPresenterImpl(TaskManager view) {
        this.mManager = view;
        this.mTasksList = new ArrayList<>();
        this.mAPIServiceExecutor = User.getActiveUser().getExecutor();
        this.mDataExporter = new BackupManager();
        this.mDbAsyncExecutor = DbTasks.getInstance().getAsyncExecutor();
    }

    @Override
    public void taskIsCompleted(int id) {
        long timestamp = System.currentTimeMillis();

        TaskEntry updatedEntry = new TaskEntry(id)
                .setTtl(timestamp)
                .setEdited(timestamp);

        mAPIServiceExecutor.updateEntry(updatedEntry, new ExecuteControllerAdapter<TaskEntry>() {
            @Override
            public void onFinish() {
                notifyDataUpdate();
            }
        });
    }

    @Override
    public void postponeTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler) {
        mDbAsyncExecutor.getEntryById(id, new ExecuteControllerAdapter<TaskEntry>()
        {
            @Override
            public void onResult(@NonNull TaskEntry entry) {
                coupler.bind(date -> mAPIServiceExecutor.updateEntry(
                        entry.setTtl(date.getTime())
                                .setEdited(System.currentTimeMillis()),
                        new ExecuteControllerAdapter<TaskEntry>()
                        {
                            @Override
                            public void onFinish() {
                                notifyDataUpdate();
                            }
                        }
                ), entry);
            }
        });
    }

    @Override
    public void deleteTheTask(int id) {
        mAPIServiceExecutor.removeEntryById(id, new ExecuteControllerAdapter<TaskEntry>() {
            @Override
            public void onFinish() {
                notifyDataUpdate();
            }
        });
    }

    @Override
    public void restoreTheTask(int id, Coupler<Callback<Date>, TaskEntry> coupler) {
        mDbAsyncExecutor.getEntryById(id, new ExecuteControllerAdapter<TaskEntry>()
        {
            @Override
            public void onResult(@NonNull TaskEntry entry) {
                coupler.bind(date -> {
                    entry.setTtl(date.getTime()).setEdited(System.currentTimeMillis());
                    mAPIServiceExecutor.updateEntry(entry, new ExecuteControllerAdapter<TaskEntry>() {
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
    public void editTheTask(int id, CursorProvider adapter, TasksListAdapter.ViewHolder holder) {
        mManager.startEditor(id, adapter, holder);
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
        mAPIServiceExecutor.getAllEntries(
                new DataExportController<>(
                        mManager.getBaseContext(), mManager,
                        path, EXPORTED_FILENAME,
                        mDataExporter
                )
        );
    }

    @Override
    public void importData(Uri uri) {
        InputStream inputStream;

        try {
            inputStream = mManager.getBaseContext()
                    .getContentResolver()
                    .openInputStream(uri);

            mDataExporter.importFrom(inputStream, TaskEntry.class,
                    new DataImportController<>(mManager, mDbAsyncExecutor, aVoid -> mManager.syncData())
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TaskListView bindView(TaskListView view) {
        mTasksList.add(view);
        return view;
    }

    @Override
    public TaskListView unBindView(TaskListView view) {
        mTasksList.remove(view);
        return view;
    }

    @Override
    public void onResult(int requestCode) {
        switch (requestCode) {
            case REQUEST_CREATE:
                mManager.showAlert(mManager.getResources().getString(R.string.task_was_created));
                break;
            case REQUEST_EDIT:
                mManager.showAlert(mManager.getResources().getString(R.string.task_was_updated));
                break;
        }

        notifyDataUpdate();
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

                mAPIServiceExecutor.getCursor(filter, new ExecuteControllerAdapter<Cursor>() {
                        @Override
                        public void onStart() {
                            mManager.showProgress();
                            counter.incrementAndGet();
                        }

                        @Override
                        public void onResult(@NonNull Cursor cursor) {
                            taskList.onUpdate(cursor);
                            taskList.onUpdate(filter);
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
