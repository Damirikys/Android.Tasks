package ru.urfu.taskmanager.data.backup;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenter;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.data.db.async.AsyncExecutor;
import ru.urfu.taskmanager.data.db.DbTasks;
import ru.urfu.taskmanager.data.db.DbTasksFilter;
import ru.urfu.taskmanager.data.db.async.ExecuteControllerAdapter;
import ru.urfu.taskmanager.utils.interfaces.Progressive;

public class TasksGenerator
{
    private static final int TIME_OFFSET = 60000;
    private static final int COUNT_OF_GENERATED_TASKS = 100000;
    private static final String IMAGE_URL = "https://developer.android.com/images/home/nougat_bg.jpg";

    private final TaskManagerPresenter mPresenter;
    private final Progressive mProgressive;
    private final AsyncExecutor<TaskEntry> mAsyncExecutor;
    private final TasksGeneratorController mController;
    private final DbTasks mDatabase;

    public TasksGenerator(TaskManagerPresenter presenter, Progressive progressive) {
        this.mPresenter = presenter;
        this.mDatabase = DbTasks.getInstance();
        this.mAsyncExecutor = mDatabase.getAsyncExecutor();
        this.mProgressive = progressive;
        this.mController = new TasksGeneratorController();
    }

    public void generate() {
        mAsyncExecutor.startTransaction(new ExecuteControllerAdapter<Void>()
        {
            @Override
            public void onStart() {
                mController.onStart();

                TaskEntry mock = new TaskEntry();
                long timestamp = System.currentTimeMillis();
                for (int i = 0; i < COUNT_OF_GENERATED_TASKS; i++) {
                    timestamp = timestamp + TIME_OFFSET;
                    mDatabase.insertEntry(
                            mock.setTitle(String.valueOf(timestamp))
                                    .setDescription(String.valueOf(i))
                                    .setTtl(timestamp)
                                    .setCreated(timestamp)
                                    .setEdited(timestamp)
                                    .setColor(Color.BLUE)
                                    .setImageUrl(IMAGE_URL)
                    );

                    if (i % 100 == 0) mController.onProgress(i);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mController.onFinish(null);
            }
        });
    }

    private class TasksGeneratorController extends ExecuteControllerAdapter<Void>
    {
        NotificationCompat.Builder notificationBuilder;

        @Override
        protected NotificationManager bindNotificationManager() {
            return (NotificationManager) mProgressive.getBaseContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

        @Override
        public void onStart() {
            super.onStart();
            mProgressive.startProgressIndicator(COUNT_OF_GENERATED_TASKS);
            mProgressive.showProgress();
            mProgressive.showAlert(mProgressive.getResources().getString(R.string.data_generate_started));

            notificationBuilder = new NotificationCompat.Builder(mProgressive.getApplicationContext());
            notificationBuilder.setOngoing(true)
                    .setContentTitle(mProgressive.getResources().getString(R.string.data_generate))
                    .setContentText(mProgressive.getResources().getString(R.string.generated) + " " + 0)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setProgress(COUNT_OF_GENERATED_TASKS, 0, false);

            getNotificationManager().notify(getNotificationID(), notificationBuilder.build());
        }

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            mProgressive.setProgressIndicatorValue(value);
            getNotificationManager().notify(getNotificationID(),
                    notificationBuilder
                            .setContentText(mProgressive.getResources().getString(R.string.generated) + " " + value)
                            .setProgress(COUNT_OF_GENERATED_TASKS, value, false)
                            .build()
            );
        }

        @Override
        public void onFinish() {
            mProgressive.stopProgressIndicator();
            mProgressive.hideProgress();
            mPresenter.applyFilter(DbTasksFilter.DEFAULT_BUILDER);
            mProgressive.showAlert(mProgressive.getResources().getString(R.string.data_is_generated));
            getNotificationManager().cancel(getNotificationID());
        }
    }
}
