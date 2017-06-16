package ru.urfu.taskmanager.view.editor.presenter;

import ru.urfu.taskmanager.entities.TaskEntry;

public interface TaskEditorPresenter
{
    void saveState(TaskEntry state);
}
