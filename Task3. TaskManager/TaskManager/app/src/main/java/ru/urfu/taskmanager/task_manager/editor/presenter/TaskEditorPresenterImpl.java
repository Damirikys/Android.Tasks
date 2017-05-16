package ru.urfu.taskmanager.task_manager.editor.presenter;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.auth.models.User;
import ru.urfu.taskmanager.color_picker.recent.RecentColorsStorage;
import ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.task_manager.editor.view.TaskEditor;
import ru.urfu.taskmanager.data.db.DbTasks;
import ru.urfu.taskmanager.data.db.DbTasksHelper;
import ru.urfu.taskmanager.data.network.APIServiceExecutor;
import ru.urfu.taskmanager.data.db.async.DbAsyncExecutor;
import ru.urfu.taskmanager.data.db.async.ExecuteControllerAdapter;
import ru.urfu.taskmanager.utils.interfaces.Callback;

import static android.app.Activity.RESULT_OK;

public class TaskEditorPresenterImpl implements TaskEditorPresenter
{
    private final int INVALID_ID = -1;

    private int mItemId;
    private int mEntryId;

    private TaskEditor mEditor;
    private TaskValidator mValidator;
    private DbAsyncExecutor<TaskEntry> mDbAsyncExecutor;
    private APIServiceExecutor mAPIServiceExecutor;
    private RecentColorsStorage mRecentColorsStorage;

    public TaskEditorPresenterImpl(TaskEditor editor) {
        this.mEditor = editor;
        this.mAPIServiceExecutor = User.getActiveUser().getExecutor();
        this.mDbAsyncExecutor = DbTasks.getInstance().getAsyncExecutor();
        this.mRecentColorsStorage = RecentColorsStorage.getRepository();
        this.mValidator = new TaskValidator();
        init();
    }

    private void init() {
        if (mEditor.getIntent().getAction().equals(TaskManagerActivity.ACTION_EDIT)) {
            mEditor.setToolbarTitle(mEditor.getResources().getString(R.string.editor_edit_title));
            mItemId = mEditor.getIntent().getIntExtra(DbTasksHelper.ID, INVALID_ID);
            if (mItemId != INVALID_ID) {
                mDbAsyncExecutor.getEntryById(mItemId,
                        new ExecuteControllerAdapter<TaskEntry>() {
                            @Override
                            public void onFinish(TaskEntry result) {
                                mEntryId = result.getEntryId();

                                if (!mEditor.isRestored())
                                    mEditor.initializeEditor(result);
                                mEditor.onComplete(result);
                            }
                        });
            }
        }
    }

    @Override
    public void saveState(TaskEntry state) {
        mValidator.validate(state, aVoid -> {
            long timestamp = System.currentTimeMillis();
            state.setId(mItemId).setEntryId(mEntryId).setEdited(timestamp);

            switch (mEditor.getIntent().getAction()) {
                case TaskManagerActivity.ACTION_CREATE:
                    mAPIServiceExecutor.insertEntry(state.setCreated(timestamp),
                            new ExecuteControllerAdapter<TaskEntry>()
                            {
                                @Override
                                public void onFinish() {
                                    mRecentColorsStorage.putItem(state.getColorInt());
                                    mEditor.exit(RESULT_OK);
                                }
                            }
                    );
                    break;
                case TaskManagerActivity.ACTION_EDIT:
                    mAPIServiceExecutor.updateEntry(state,
                            new ExecuteControllerAdapter<TaskEntry>()
                            {
                                @Override
                                public void onFinish() {
                                    mRecentColorsStorage.putItem(state.getColorInt());
                                    mEditor.exit(RESULT_OK);
                                }
                            }
                    );
                    break;
            }
        });
    }

    private class TaskValidator {
        private static final int TITLE_MAX_LENGTH = 20;
        private static final int DESCRIPTION_MAX_LENGTH = 50;

        private boolean isValid = true;

        private void validate(TaskEntry entry, Callback<Void> callback) {
            isValid = true;

            if (entry.getTitle().isEmpty()) {
                isValid = false;
                mEditor.showTitleError(mEditor.getResources().getString(R.string.search_hint));
            }

            if (entry.getTitle().length() > TITLE_MAX_LENGTH) {
                isValid = false;
                mEditor.showTitleError(mEditor.getResources().getString(R.string.incorrect_length) + " " + TITLE_MAX_LENGTH);
            }

            if (entry.getDescription().isEmpty()) {
                isValid = false;
                mEditor.showDescriptionError(mEditor.getResources().getString(R.string.entry_description));
            }

            if (entry.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
                isValid = false;
                mEditor.showTitleError(mEditor.getResources().getString(R.string.incorrect_length) + " " + DESCRIPTION_MAX_LENGTH);
            }

            if (isValid) callback.call(null);
        }
    }
}
