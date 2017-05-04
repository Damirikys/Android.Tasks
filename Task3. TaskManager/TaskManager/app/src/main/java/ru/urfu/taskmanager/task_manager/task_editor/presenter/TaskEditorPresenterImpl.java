package ru.urfu.taskmanager.task_manager.task_editor.presenter;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.color_picker.recent.RecentColorsStorage;
import ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.task_manager.task_editor.view.TaskEditor;
import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.utils.db.TasksDatabaseHelper;
import ru.urfu.taskmanager.utils.interfaces.Callback;

import static android.app.Activity.RESULT_OK;

public class TaskEditorPresenterImpl implements TaskEditorPresenter
{
    private final int INVALID_ID = -1;

    private int mItemId;

    private TaskEditor mEditor;
    private TaskValidator mValidator;
    private TasksDatabase mDatabase;
    private RecentColorsStorage mRecentColorsStorage;

    public TaskEditorPresenterImpl(TaskEditor editor) {
        this.mEditor = editor;
        this.mDatabase = TasksDatabase.getInstance();
        this.mRecentColorsStorage = RecentColorsStorage.getRepository();
        this.mValidator = new TaskValidator();
        init();
    }

    private void init() {
        if (mEditor.getIntent().getAction().equals(TaskManagerActivity.ACTION_EDIT)) {
            mEditor.setToolbarTitle(mEditor.getResources().getString(R.string.editor_edit_title));
            mItemId = mEditor.getIntent().getIntExtra(TasksDatabaseHelper.ID, INVALID_ID);
            if (mItemId != INVALID_ID) {
                TaskEntry entryToEdit = mDatabase.getEntryById(mItemId);
                if (!mEditor.isRestored())
                    mEditor.initializeEditor(entryToEdit);
            }
        }
    }

    @Override
    public void saveState(TaskEntry state) {
        mValidator.validate(state, aVoid -> {
            long timestamp = System.currentTimeMillis();

            switch (mEditor.getIntent().getAction()) {
                case TaskManagerActivity.ACTION_CREATE:
                    mDatabase.insertEntry(state.
                            setId(mItemId)
                            .setCreated(timestamp)
                            .setEdited(timestamp)
                    );
                    break;
                case TaskManagerActivity.ACTION_EDIT:
                    mDatabase.updateEntry(state.
                            setId(mItemId)
                            .setEdited(timestamp)
                            .setCreated(mDatabase.getEntryById(mItemId).getCreatedTimestamp())
                    );
                    break;
            }

            mRecentColorsStorage.putItem(state.getColorInt());
            mEditor.exit(RESULT_OK);
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
