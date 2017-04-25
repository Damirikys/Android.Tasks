package ru.urfu.taskmanager.task_manager.task_editor.presenter;

import ru.urfu.taskmanager.Application;
import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.task_manager.task_editor.view.TaskEditor;
import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.utils.db.TasksDatabaseHelper;
import ru.urfu.taskmanager.utils.interfaces.Callback;

import static android.app.Activity.RESULT_OK;

public class TaskEditorPresenterImpl implements TaskEditorPresenter
{
    private int itemId;

    private TaskEditor editor;
    private TaskValidator validator;
    private TasksDatabase database;

    public TaskEditorPresenterImpl(TaskEditor editor) {
        this.editor = editor;
        this.database = TasksDatabase.getInstance();
        this.validator = new TaskValidator();
        init();
    }

    private void init() {
        if (editor.getIntent().getAction().equals(TaskManagerActivity.ACTION_EDIT))
        {
            editor.setToolbarTitle(Application.getContext().getString(R.string.editor_edit_title));
            itemId = editor.getIntent().getIntExtra(TasksDatabaseHelper.ID, -1);
            if (itemId != -1) {
                TaskEntry entryToEdit = database.getEntryById(itemId);
                if(!editor.isRestored())
                    editor.initializeEditor(entryToEdit);
            }
        }
    }

    @Override
    public void saveState(TaskEntry state) {
        validator.validate(state, aVoid -> {
            switch (editor.getIntent().getAction()) {
                case TaskManagerActivity.ACTION_CREATE:
                    database.insertEntry(state.setId(itemId));
                    break;
                case TaskManagerActivity.ACTION_EDIT:
                    database.updateEntry(state.setId(itemId));
                    break;
            }

            editor.exit(RESULT_OK);
        });
    }

    private class TaskValidator
    {
        private static final int TITLE_MAX_LENGTH = 20;
        private static final int DESCRIPTION_MAX_LENGTH = 50;

        private boolean isValid = true;

        private void validate(TaskEntry entry, Callback<Void> callback) {
            isValid = true;

            if (entry.getTitle().isEmpty()) {
                isValid = false;
                editor.showTitleError("Введите название");
            }

            if (entry.getTitle().length() > TITLE_MAX_LENGTH) {
                isValid = false;
                editor.showTitleError("Длина название не должна превышать " + TITLE_MAX_LENGTH);
            }

            if (entry.getDescription().isEmpty()) {
                isValid = false;
                editor.showDescriptionError("Введите примечание");
            }

            if (entry.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
                isValid = false;
                editor.showTitleError("Длина примечания не должна превышать " + DESCRIPTION_MAX_LENGTH);
            }

            if (isValid) callback.call(null);
        }
    }
}
