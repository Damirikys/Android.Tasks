package ru.urfu.taskmanager.task_manager.task_editor.presenter;

import ru.urfu.taskmanager.task_manager.models.TaskEntry;

public interface TaskEditorPresenter {
    void saveState(TaskEntry state);
}
