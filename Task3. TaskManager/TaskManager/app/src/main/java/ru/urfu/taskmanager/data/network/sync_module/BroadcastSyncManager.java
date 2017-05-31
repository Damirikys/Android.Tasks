package ru.urfu.taskmanager.data.network.sync_module;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.auth.models.User;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.task_manager.models.TaskEntryCouples;
import ru.urfu.taskmanager.utils.tools.NetworkUtil;

public abstract class BroadcastSyncManager extends BroadcastReceiver
{
    public static final String SYNC_AT_FIRST_TIME_KEY = "ru.urfu.taskmanager.SYNC_AT_FIRST_TIME_KEY";

    private static final int SYNC_SCHEDULED = 1;
    private static final int SYNC_NOT_SCHEDULED = 2;
    private static final int SYNC_AT_FIRST_TIME = 3;

    public static final String SYNC_SCHEDULE_ACTION = "ru.urfu.taskmanager.SYNC_SCHEDULE_ACTION";
    public static final String SYNC_SUCCESS_ACTION = "ru.urfu.taskmanager.SYNC_SUCCESS_ACTION";
    public static final String SYNC_FAILED_ACTION = "ru.urfu.taskmanager.SYNC_FAILED_ACTION";
    public static final String SYNC_START_ACTION = "ru.urfu.taskmanager.SYNC_START_ACTION";
    public static final String SYNC_ASK_ACTION = "ru.urfu.taskmanager.SYNC_ASK_ACTION";

    private static final String REPOSITORY_NAME = "ru.urfu.taskmanager.API_SYNC";

    private Context mContext;
    private MergeConflictsSolver mMergeConflictsSolver;
    private SharedPreferences mSyncRepository;

    public BroadcastSyncManager(Activity activity) {
        this.mContext = activity;
        this.mSyncRepository = mContext.getSharedPreferences(REPOSITORY_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public final void onReceive(final Context context, final Intent data) {
        switch (data.getAction()) {
            case SYNC_SCHEDULE_ACTION:
                onScheduleSync();
                break;
            case SYNC_SUCCESS_ACTION:
                unScheduleSync();
                onStopSync();
                break;
            case SYNC_FAILED_ACTION:
                Toast.makeText(context, context.getString(R.string.sync_failed), Toast.LENGTH_SHORT).show();
                onScheduleSync();
                onStopSync();
                break;
            case SYNC_ASK_ACTION:
                Bundle bundle = data.getExtras();
                startConflictsSolver(bundle.getParcelable(SynchronizeService.EXTRA_KEY));
                break;
            case SYNC_START_ACTION:
                synchronizeData(SYNC_SCHEDULED);
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                synchronizeDataIfScheduled();
                break;
        }
    }

    private void startConflictsSolver(TaskEntryCouples mergeCouples) {

        mMergeConflictsSolver = new MergeConflictsSolver(mContext, mergeCouples);

        for (TaskEntryCouples.Couple couple : mergeCouples)
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.sync_conflict_layout, null);

            ViewGroup leftLayout = (ViewGroup) view.findViewById(R.id.first_container);
            ViewGroup rightLayout = (ViewGroup) view.findViewById(R.id.second_container);

            fillLayout(leftLayout, couple.getKey());
            fillLayout(rightLayout, couple.getValue());

            BottomDialog solverDialog = new BottomDialog.Builder(mContext)
                    .setTitle(mContext.getString(R.string.merge_conflict_title))
                    .setContent(mContext.getString(R.string.merge_conflict_descr))
                    .setCustomView(view)
                    .setCancelable(false)
                    .build();


            leftLayout.setOnClickListener(mMergeConflictsSolver.with(couple, MergeConflictsSolver.LOCAL,
                    aVoid -> solverDialog.dismiss())
            );

            rightLayout.setOnClickListener(mMergeConflictsSolver.with(couple, MergeConflictsSolver.REMOTE,
                    aVoid -> solverDialog.dismiss())
            );

            solverDialog.show();
        }
    }

    public abstract void onStopSync();

    private void fillLayout(ViewGroup viewGroup, TaskEntry entry) {
        viewGroup.addView(createTextView("Title: " + entry.getTitle()));
        viewGroup.addView(createTextView("Descr: " + entry.getDescription()));
        viewGroup.addView(createTextView("Created: " + entry.getCreated()));
        viewGroup.addView(createTextView("Edited: " + entry.getEdited()));
        viewGroup.addView(createTextView("Viewed: " + entry.getTtl()));
        viewGroup.addView(createTextView("ImgUrl: " + entry.getImageUrl()));
        viewGroup.addView(createTextView("Color: " + entry.getColor()));
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(mContext);
        textView.setText(text);
        return textView;
    }

    private void onScheduleSync() {
        if (mMergeConflictsSolver != null)
            mMergeConflictsSolver.conflictWasNotResolved();

        mSyncRepository.edit()
                .putInt(User.getActiveUser().getLogin(), SYNC_SCHEDULED)
                .apply();
    }

    private void unScheduleSync() {
        mSyncRepository.edit()
                .putInt(User.getActiveUser().getLogin(), SYNC_NOT_SCHEDULED)
                .apply();

        Toast.makeText(mContext, mContext.getString(R.string.sync_success), Toast.LENGTH_SHORT).show();
    }

    private void synchronizeDataIfScheduled() {
        int type = mSyncRepository.getInt(User.getActiveUser().getLogin(), SYNC_AT_FIRST_TIME);
        synchronizeData(type);
    }

    private void synchronizeData(int type) {
        if (NetworkUtil.networkIsReachable(mContext)) {
            switch (type) {
                case SYNC_SCHEDULED:
                    Toast.makeText(mContext, mContext.getString(R.string.sync_title), Toast.LENGTH_SHORT).show();
                    mContext.startService(new Intent(mContext, SynchronizeService.class));
                    break;
                case SYNC_AT_FIRST_TIME:
                    Toast.makeText(mContext, mContext.getString(R.string.sync_title), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, SynchronizeService.class);
                    intent.putExtra(SYNC_AT_FIRST_TIME_KEY, true);
                    mContext.startService(intent);
                    break;
            }
        } else if (type == SYNC_AT_FIRST_TIME) {
            onScheduleSync();
        }
    }
}
