package ru.urfu.taskmanager.sync;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ru.urfu.taskmanager.entities.User;
import ru.urfu.taskmanager.entities.TaskEntry;
import ru.urfu.taskmanager.db.DbTasks;
import ru.urfu.taskmanager.entities.TaskEntryCouples;
import ru.urfu.taskmanager.network.APICallback;
import ru.urfu.taskmanager.network.APIResponse;
import ru.urfu.taskmanager.network.APIService;
import ru.urfu.taskmanager.interfaces.Thenable;

public class SynchronizeService extends Service implements Thenable<Void>
{
    public static final String EXTRA_KEY = "data";

    private TaskEntryCouples mDisputableEntries;
    private SparseArray<TaskEntry> mRemoteMapEntries;

    private APIService<TaskEntry> mApiService;
    private DbTasks mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mDisputableEntries = new TaskEntryCouples();
        mRemoteMapEntries = new SparseArray<>();
        mApiService = User.getActiveUser().getService();
        mDatabase = DbTasks.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        boolean loadAll = intent.getBooleanExtra(BroadcastSyncManager.SYNC_AT_FIRST_TIME_KEY, false);

        if (loadAll) {
            loadDataProcess();
        } else {
            startSyncProcess();
        }

        return START_NOT_STICKY;
    }

    private void loadDataProcess() {
        mApiService.getUserNotes().send(new APICallback<Collection<TaskEntry>>()
        {
            @Override
            public void onResponse(APIResponse<Collection<TaskEntry>> response) {
                mDatabase.startTransaction(aVoid -> {
                    for (TaskEntry entry : response.getBody()) {
                        mDatabase.insertEntry(entry);
                    }
                });

                SynchronizeService.this.onSuccess();
            }

            @Override
            public void onFailure(Throwable t) {
                SynchronizeService.this.onFailed(t);
            }
        });
    }

    private void startSyncProcess() {
        List<TaskEntry> localEntries = mDatabase.getAllEntries();

        mApiService.getUserNotes().send(new APICallback<Collection<TaskEntry>>()
        {
            @Override
            public void onResponse(APIResponse<Collection<TaskEntry>> response)
            {
                for (TaskEntry entry : response.getBody()) {
                    mRemoteMapEntries.put(entry.getEntryId(), entry);
                }

                Queue<TaskEntry> localQueue = new ConcurrentLinkedQueue<>(localEntries);
                while (!localQueue.isEmpty())
                {
                    TaskEntry localEntry = localQueue.poll();
                    TaskEntry remoteEntry = mRemoteMapEntries.get(localEntry.getEntryId());

                    if (remoteEntry != null) {
                        if (localEntry.hashCode() == remoteEntry.hashCode()) {
                            mRemoteMapEntries.remove(localEntry.getEntryId());
                            continue;
                        }

                        if (localEntry.getDeviceIdentifier()
                                .equals(remoteEntry.getDeviceIdentifier())) {
                            if (localEntry.getEditedTimestamp() > remoteEntry.getEditedTimestamp()) {
                                mApiService.editNote(localEntry, localEntry.getEntryId()).send(new APICallback<Void>()
                                {
                                    @Override
                                    public void onFailure(Throwable t) {
                                        SynchronizeService.this.onFailed(t);
                                    }
                                });
                            } else {
                                mDisputableEntries.put(localEntry, remoteEntry);
                            }
                        } else {
                            mDisputableEntries.put(localEntry, remoteEntry);
                        }

                        mRemoteMapEntries.remove(localEntry.getEntryId());
                    } else {
                        mApiService.createNote(localEntry)
                                .send(new APICallback<Integer>()
                                {
                                    @Override
                                    public void onResponse(APIResponse<Integer> response) {
                                        mDatabase.updateEntry(
                                                localEntry.setEntryId(
                                                        response.getBody()
                                                )
                                        );
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        SynchronizeService.this.onFailed(t);
                                    }
                                });
                    }
                }

                while (mRemoteMapEntries.size() != 0) {
                    TaskEntry remoteEntry = mRemoteMapEntries.valueAt(0);
                    if (remoteEntry.getDeviceIdentifier()
                            .equals(User.getActiveUser().getDeviceIdentifier())) {
                        mApiService.deleteNote(remoteEntry.getEntryId())
                                .send(new APICallback<Void>()
                                {
                                    @Override
                                    public void onFailure(Throwable t) {
                                        SynchronizeService.this.onFailed(t);
                                    }
                                });
                    } else {
                        mDatabase.insertEntry(remoteEntry);
                    }

                    mRemoteMapEntries.removeAt(0);
                }

                SynchronizeService.this.onSuccess();
            }

            @Override
            public void onFailure(Throwable t) {
                SynchronizeService.this.onFailed(t);
            }
        });
    }

    @Override
    public void onSuccess(Void... results) {
        if (!mDisputableEntries.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_KEY, mDisputableEntries);

            Intent intent = new Intent(BroadcastSyncManager.SYNC_ASK_ACTION);
            intent.putExtras(bundle);

            sendBroadcast(intent);
        } else {
            sendBroadcast(new Intent(BroadcastSyncManager.SYNC_SUCCESS_ACTION));
        }

        stopSelf();
    }

    @Override
    public void onFailed(Throwable t) {
        t.printStackTrace();
        sendBroadcast(new Intent(BroadcastSyncManager.SYNC_FAILED_ACTION));
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
