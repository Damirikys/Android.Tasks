package ru.urfu.taskmanager.task_manager.editor.presenter;

import android.support.annotation.NonNull;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.auth.models.User;
import ru.urfu.taskmanager.color_picker.recent.RecentColorsStorage;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.task_manager.editor.view.TaskEditor;
import ru.urfu.taskmanager.data.db.DbTasks;
import ru.urfu.taskmanager.data.network.APIServiceExecutor;
import ru.urfu.taskmanager.data.db.async.DbAsyncExecutor;
import ru.urfu.taskmanager.data.db.async.ExecuteControllerAdapter;
import ru.urfu.taskmanager.utils.interfaces.Callback;

import static ru.urfu.taskmanager.task_manager.editor.view.TaskEditorFragment.NON_ID;

public class TaskEditorPresenterImpl implements TaskEditorPresenter
{
    private int mItemId;
    private int mEntryId;

    private TaskEditor mEditor;
    private TaskValidator mValidator;
    private DbAsyncExecutor<TaskEntry> mDbAsyncExecutor;
    private APIServiceExecutor mAPIServiceExecutor;
    private RecentColorsStorage mRecentColorsStorage;

    public TaskEditorPresenterImpl(TaskEditor editor) {
        this.mEditor = editor;
        this.mItemId = NON_ID;
        this.mAPIServiceExecutor = User.getActiveUser().getExecutor();
        this.mDbAsyncExecutor = DbTasks.getInstance().getAsyncExecutor();
        this.mRecentColorsStorage = RecentColorsStorage.getRepository();
        this.mValidator = new TaskValidator();
        init();
    }

    private void init() {
        if (mEditor.getEditedItemId() != NON_ID) {
            mItemId = mEditor.getEditedItemId();

            mDbAsyncExecutor.getEntryById(mItemId,
                    new ExecuteControllerAdapter<TaskEntry>() {
                        @Override
                        public void onResult(@NonNull TaskEntry result) {
                            mEntryId = result.getEntryId();
                            mEditor.initializeEditor(result);
                            mEditor.onImageLoad(result.getImageUrl());
                        }
                    });
        }
    }

    @Override
    public void saveState(TaskEntry state) {
        ExecuteControllerAdapter<TaskEntry> onPostExecute = new ExecuteControllerAdapter<TaskEntry>() {
            @Override
            public void onFinish() {
                mRecentColorsStorage.putItem(state.getColorInt());
                mEditor.exit();
            }
        };

        mValidator.validate(state, aVoid -> {
            long timestamp = System.currentTimeMillis();
            state.setId(mItemId).setEntryId(mEntryId).setEdited(timestamp);

            switch (mItemId) {
                case NON_ID:
                    mAPIServiceExecutor.insertEntry(state.setCreated(timestamp), onPostExecute);
                    break;
                default:
                    mAPIServiceExecutor.updateEntry(state, onPostExecute);
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
                mEditor.showTitleError(mEditor.getString(R.string.search_hint));
            }

            if (entry.getTitle().length() > TITLE_MAX_LENGTH) {
                isValid = false;
                mEditor.showTitleError(mEditor.getString(R.string.incorrect_length) + " " + TITLE_MAX_LENGTH);
            }

            if (entry.getDescription().isEmpty()) {
                isValid = false;
                mEditor.showDescriptionError(mEditor.getString(R.string.entry_description));
            }

            if (entry.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
                isValid = false;
                mEditor.showTitleError(mEditor.getString(R.string.incorrect_length) + " " + DESCRIPTION_MAX_LENGTH);
            }

            if (isValid) callback.call(null);
        }
    }
}
