package ru.urfu.taskmanager.task_manager.task_editor.presenter;

import ru.urfu.taskmanager.Application;
import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.task_editor.view.TaskEditor;
import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.utils.db.TasksDatabaseHelper;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity;

public class TaskEditorPresenterImpl implements TaskEditorPresenter
{
    private TaskEditor editor;
    private TasksDatabase database;

    private int itemId;

    public TaskEditorPresenterImpl(TaskEditor editor) {
        this.editor = editor;
        this.database = TasksDatabase.getInstance();
        init();
    }

    private void init() {
        if (editor.getIntent().getAction().equals(TaskManagerActivity.ACTION_EDIT))
        {
            editor.setToolbarTitle(Application.getContext().getString(R.string.editor_edit_title));
            itemId = editor.getIntent().getIntExtra(TasksDatabaseHelper.ID, -1);
            if (itemId != -1) {
                TaskEntry entryToEdit = database.getEntryById(itemId);
                editor.initializeEditor(entryToEdit);
            }
        }
    }

    @Override
    public void saveState(TaskEntry state) {
        state.setId(itemId);

        switch (editor.getIntent().getAction()) {
            case TaskManagerActivity.ACTION_CREATE:
                database.insertEntry(state);
                break;
            case TaskManagerActivity.ACTION_EDIT:
                database.updateEntry(state);
                break;
        }
    }
}
