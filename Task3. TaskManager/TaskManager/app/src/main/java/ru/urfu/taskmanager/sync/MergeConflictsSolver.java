package ru.urfu.taskmanager.sync;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.entities.User;
import ru.urfu.taskmanager.network.APIServiceExecutor;
import ru.urfu.taskmanager.entities.TaskEntryCouples;
import ru.urfu.taskmanager.interfaces.Callback;

public final class MergeConflictsSolver
{
    public static final int LOCAL = 1;
    public static final int REMOTE = 2;

    private Context mContext;
    private TaskEntryCouples mCouples;
    private List<OnSolveListener> mListeners;

    private boolean isResolved = true;

    public MergeConflictsSolver(Context context, TaskEntryCouples couples) {
        this.mContext = context;
        this.mCouples = couples;
        this.mListeners = new ArrayList<>();
    }

    public OnSolveListener with(TaskEntryCouples.Couple couple, int type, Callback<Void> callback) {
        OnSolveListener onSolveListener = new OnSolveListener(couple, type, callback);
        mListeners.add(onSolveListener);
        return onSolveListener;
    }

    public void conflictWasNotResolved() {
        isResolved = false;

        for (OnSolveListener mListener : mListeners) {
            mListener.mCallback.call(null);
        }

        Toast.makeText(mContext, mContext.getString(R.string.conflict_not_resolved), Toast.LENGTH_SHORT).show();
    }

    private class OnSolveListener implements View.OnClickListener
    {
        int mType;
        TaskEntryCouples.Couple mCouple;
        Callback<Void> mCallback;

        APIServiceExecutor mApiService;

        OnSolveListener(TaskEntryCouples.Couple couple, int type, Callback<Void> callback) {
            this.mType = type;
            this.mCouple = couple;
            this.mCallback = callback;
            this.mApiService = User.getActiveUser()
                    .getExecutor();
        }

        @Override
        public void onClick(View v) {
            switch (mType) {
                case LOCAL:
                    mApiService.updateEntry(mCouple.getKey());
                    break;
                case REMOTE:
                    mApiService.updateEntry(mCouple.getValue()
                            .setId(mCouple.getKey().getId())
                            .setDeviceIdentifier(User.getActiveUser().getDeviceIdentifier())
                    );
                    break;
            }

            MergeConflictsSolver.this.mCouples.remove(mCouple);
            if (MergeConflictsSolver.this.mCouples.size() == 0 && isResolved) {
                MergeConflictsSolver.this.mContext
                        .sendBroadcast(new Intent(BroadcastSyncManager.SYNC_SUCCESS_ACTION));
            }

            mCallback.call(null);
        }
    }
}
