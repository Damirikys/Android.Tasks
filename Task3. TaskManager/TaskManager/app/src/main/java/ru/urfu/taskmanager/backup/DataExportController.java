package ru.urfu.taskmanager.backup;

import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import java.util.List;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.db.async.ExecuteControllerAdapter;
import ru.urfu.taskmanager.interfaces.Progressive;

public class DataExportController<T> extends ExecuteControllerAdapter<List<T>>
{
    private static final int MAX_PROGRESS_VALUE = 100;

    private Context mContext;
    private Progressive mProgressive;
    private BackupManager mDataExporter;
    private String mPath, mFileName;

    private NotificationCompat.Builder mNotificationBuilder;

    public DataExportController(Context context,
                                Progressive progressive,
                                String path,
                                String filename,
                                BackupManager exporter)
    {
        this.mContext = context;
        this.mProgressive = progressive;
        this.mDataExporter = exporter;
        this.mPath = path;
        this.mFileName = filename;
    }

    @Override
    public void onStart() {
        super.onStart();
        mProgressive.startProgressIndicator(MAX_PROGRESS_VALUE);
        mProgressive.showAlert(mContext.getString(R.string.data_collection_started));

        mNotificationBuilder = new NotificationCompat.Builder(mProgressive.getApplicationContext());
        mNotificationBuilder.setOngoing(true)
                .setContentTitle(mContext.getString(R.string.data_export))
                .setContentText(mContext.getString(R.string.data_collected) + " " + 0 + "%")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setProgress(MAX_PROGRESS_VALUE, 0, false);

        getNotificationManager().notify(getNotificationID(), mNotificationBuilder.build());
    }

    @Override
    public void onProgress(int value) {
        super.onProgress(value);
        mProgressive.setProgressIndicatorValue(value);
        getNotificationManager().notify(getNotificationID(),
                mNotificationBuilder
                        .setContentText(mContext.getString(R.string.data_collected) + " " + value + "%")
                        .setProgress(MAX_PROGRESS_VALUE, value, false)
                        .build()
        );
    }

    @Override
    public void onResult(@NonNull List<T> result) {
        mProgressive.stopProgressIndicator();

        mDataExporter.exportTo(new BackupManager.DataProvider<>(
                mPath, mFileName,
                result, new JsonExporterController()
        ));
    }

    @Override
    protected int getNotificationID() {
        return super.getNotificationID();
    }

    @Override
    protected NotificationManager bindNotificationManager() {
        return (NotificationManager) mProgressive.getBaseContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private class JsonExporterController extends ExecuteControllerAdapter<Boolean>
    {
        @Override
        public void onStart() {
            DataExportController.this.getNotificationManager()
                    .notify(DataExportController.this.getNotificationID(),
                            mNotificationBuilder
                                    .setContentText(mContext.getString(R.string.data_is_saved))
                                    .setProgress(0, 0, true)
                                    .build()
            );
        }

        @Override
        public void onResult(@NonNull Boolean result) {
            if (result) {
                mProgressive.showAlert(mContext
                        .getString(R.string.task_successful_export));
            } else {
                mProgressive.showAlert(mContext
                        .getString(R.string.task_export_failed));
            }

            mProgressive.hideProgress();
            DataExportController.this.getNotificationManager()
                    .cancel(DataExportController.this.getNotificationID());
        }
    }
}
