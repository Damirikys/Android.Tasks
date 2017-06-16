package ru.urfu.taskmanager.db.async;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.urfu.taskmanager.db.filter.DbFilter;
import ru.urfu.taskmanager.db.filter.DbTasksFilter;
import ru.urfu.taskmanager.db.SimpleDatabase;

public class DbAsyncExecutor<T> extends HandlerThread implements AsyncExecutor<T>
{
    private SimpleDatabase<T> mDatabase;
    private Handler mWorkerHandler;

    public DbAsyncExecutor(SimpleDatabase<T> database) {
        super(database.toString());
        this.mDatabase = database;
        this.start();
    }


    @Override
    protected void onLooperPrepared() {
        mWorkerHandler = new Handler(getLooper());
    }

    @Override
    public void getEntryById(int id, ExecuteController<T> controller) {
        mWorkerHandler.post(() -> {
            controller.onStart();
            controller.onFinish(mDatabase.getEntryById(id));
        });
    }

    @Override
    public void getAllEntries(@NonNull ExecuteController<List<T>> executeController) {
        new AsyncTask<Void, Integer, List<T>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                executeController.onStart();
            }

            @Override
            protected List<T> doInBackground(Void... params)
            {
                final List<T> entries = new ArrayList<>();
                final Cursor cursor = mDatabase.getCursor(DbTasksFilter.DEFAULT_BUILDER.build());
                final double totalCount = cursor.getCount();
                if (totalCount == 0.0) return entries;

                cursor.moveToFirst();
                mDatabase.startTransaction(obj -> {
                    int percent = 0;

                    do {
                        entries.add(mDatabase.getCurrentEntryFromCursor(cursor));

                        int newPercent = (int) (((double) cursor.getPosition() / totalCount) * 100.0);
                        if (newPercent != percent) {
                            percent = newPercent;
                            publishProgress(newPercent);
                        }
                    } while (cursor.moveToNext());
                });

                return entries;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                executeController.onProgress(values[0]);
            }

            @Override
            protected void onPostExecute(List<T> ts) {
                super.onPostExecute(ts);
                executeController.onFinish(ts);
            }
        }.execute();
    }

    @Override
    public void insertEntry(@NonNull T entry) {
        mWorkerHandler.post(() -> mDatabase.insertEntry(entry));
    }

    @Override
    public void insertEntry(@NonNull T entry, @NonNull ExecuteController<T> controller) {
        mWorkerHandler.post(() -> {
            controller.onStart();
            T inserted = mDatabase.getEntryById((int) mDatabase.insertEntry(entry));
            controller.onFinish(inserted);
        });
    }

    @Override
    public void removeEntryById(int id) {
        mWorkerHandler.post(() -> mDatabase.removeEntryById(id));
    }

    @Override
    public void removeEntryById(int id, @NonNull ExecuteController<T> controller) {
        mWorkerHandler.post(() -> {
            controller.onStart();
            T entry = mDatabase.getEntryById(id);
            mDatabase.removeEntryById(id);
            controller.onFinish(entry);
        });
    }

    @Override
    public void updateEntry(@NonNull T entry) {
        mWorkerHandler.post(() -> mDatabase.updateEntry(entry));
    }

    @Override
    public void updateEntry(@NonNull T entry, @NonNull ExecuteController<T> controller) {
        mWorkerHandler.post(() -> {
            controller.onStart();
            controller.onFinish(mDatabase.updateEntry(entry));
        });
    }

    @Override
    public void replaceAll(@NonNull List<T> entries, @NonNull ExecuteController<Void> controller) {
        mWorkerHandler.post(() -> {
            controller.onStart();
            mDatabase.replaceAll(entries);
            controller.onFinish(null);
        });
    }

    @Override
    public void getCursor(@NonNull DbFilter filter, @NonNull ExecuteController<Cursor> controller) {
        mWorkerHandler.post(() -> {
            controller.onStart();
            Cursor cursor = mDatabase.getCursor(filter);
            cursor.getCount(); // init data
            controller.onFinish(cursor);
        });
    }

    @Override
    public void startTransaction(ExecuteController<Void> controller) {
        mWorkerHandler.post(() -> mDatabase.startTransaction(obj -> {
            controller.onStart();
            controller.onFinish(null);
        }));
    }
}
