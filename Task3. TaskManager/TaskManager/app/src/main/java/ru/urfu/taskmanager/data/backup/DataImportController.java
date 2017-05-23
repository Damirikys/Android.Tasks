package ru.urfu.taskmanager.data.backup;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import java.util.List;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.data.db.async.DbAsyncExecutor;
import ru.urfu.taskmanager.data.db.async.ExecuteControllerAdapter;
import ru.urfu.taskmanager.task_manager.main.view.TaskManager;
import ru.urfu.taskmanager.utils.interfaces.Callback;

public class DataImportController<T> extends ExecuteControllerAdapter<List<T>>
{
    private TaskManager mManager;
    private Callback<Void> mFinishImportCallback;
    private DbAsyncExecutor<T> mDbAsyncExecutor;
    private NotificationCompat.Builder mNotificationBuilder;

    public DataImportController(TaskManager manager, DbAsyncExecutor<T> dbAsyncExecutor, Callback<Void> onFinishImport) {
        this.mManager = manager;
        this.mDbAsyncExecutor = dbAsyncExecutor;
        this.mFinishImportCallback = onFinishImport;
    }

    @Override
    protected NotificationManager bindNotificationManager() {
        return (NotificationManager) mManager.getBaseContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onStart() {
        mNotificationBuilder =
                new NotificationCompat.Builder(mManager.getApplicationContext());

        mNotificationBuilder.setOngoing(true)
                .setContentTitle(mManager.getResources().getString(R.string.data_import))
                .setContentText(mManager.getResources().getString(R.string.please_wait_data_is_imported))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setProgress(0, 0, true);

        getNotificationManager().notify(getNotificationID(), mNotificationBuilder.build());
    }

    @Override
    public void onFinish(List<T> result) {
        if (result.isEmpty()) {
            mManager.hideProgress();
            mManager.showAlert(mManager.getResources()
                    .getString(R.string.task_import_failed));
            return;
        }

        mDbAsyncExecutor.replaceAll(result, new ExecuteControllerAdapter<Void>() {
            @Override
            public void onStart() {
                mManager.showProgress();

                mNotificationBuilder
                        .setContentTitle(mManager.getResources().getString(R.string.data_import))
                        .setContentText(mManager.getResources().getString(R.string.data_is_saved))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setProgress(0, 0, true);

                DataImportController.this.getNotificationManager()
                        .notify(getNotificationID(), mNotificationBuilder.build());
            }

            @Override
            public void onFinish() {
                mManager.hideProgress();
                mManager.showAlert(mManager.getResources()
                        .getString(R.string.task_successful_import));

                DataImportController.this.getNotificationManager().cancel(getNotificationID());
                mFinishImportCallback.call(null);
            }
        });
    }
}
