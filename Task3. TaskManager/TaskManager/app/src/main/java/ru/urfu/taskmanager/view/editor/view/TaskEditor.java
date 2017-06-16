package ru.urfu.taskmanager.view.editor.view;

import android.support.annotation.StringRes;
import android.view.View;

import ru.urfu.taskmanager.libs.color_picker.listeners.PickerViewStateListener;
import ru.urfu.taskmanager.entities.TaskEntry;

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
