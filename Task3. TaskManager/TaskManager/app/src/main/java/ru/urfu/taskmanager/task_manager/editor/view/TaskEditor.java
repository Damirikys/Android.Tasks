package ru.urfu.taskmanager.task_manager.editor.view;

import android.support.annotation.StringRes;
import android.view.View;

import ru.urfu.taskmanager.color_picker.listeners.PickerViewStateListener;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;

public interface TaskEditor extends View.OnClickListener, PickerViewStateListener
{
    int getEditedItemId();

    String getString(@StringRes int stringRes);

    void initializeEditor(TaskEntry entryToEdit);

    void onImageLoad(String url);

    void showTitleError(String string);

    void showDescriptionError(String string);

    void exit();
}
