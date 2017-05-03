package ru.urfu.taskmanager.color_picker.listeners;

public interface PickerViewStateListener
{
    void onColorChanged(int color);

    void editModeEnable();

    void editModeDisable();

    void theBoundaryIsReached();
}
