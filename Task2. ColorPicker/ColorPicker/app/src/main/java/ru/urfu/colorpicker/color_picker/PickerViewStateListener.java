package ru.urfu.colorpicker.color_picker;

public interface PickerViewStateListener
{
    void onColorChanged(int color);
    void editModeEnable();
    void editModeDisable();
    void theBoundaryIsReached();
}
