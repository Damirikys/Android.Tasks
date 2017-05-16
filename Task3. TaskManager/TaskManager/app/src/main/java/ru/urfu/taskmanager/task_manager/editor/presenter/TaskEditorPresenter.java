package ru.urfu.taskmanager.task_manager.editor.presenter;

import ru.urfu.taskmanager.task_manager.models.TaskEntry;

public interface TaskEditorPresenter
{
    void saveState(TaskEntry state);
}
