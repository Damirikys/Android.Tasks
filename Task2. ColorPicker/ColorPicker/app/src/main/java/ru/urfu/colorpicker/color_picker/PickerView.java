package ru.urfu.colorpicker.color_picker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

public class PickerView extends AbstractPickerView
{
    protected List<PickerViewStateListener> listeners = new ArrayList<>();

    public PickerView(Context context) {
        super(context);
    }

    public PickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setCurrentColor(int currentColor) {
        super.setCurrentColor(currentColor);
        notifySubscribers(Action.onColorChange);
    }

    public void subscribe(PickerViewStateListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(PickerViewStateListener listener) {
        listeners.remove(listener);
    }

    protected void notifySubscribers(Action state) {
        switch (state) {
            case onColorChange:
                notify(observer -> observer.onColorChanged(getCurrentColor()));
                break;
            case editModeEnable:
                if (!isEnableEditMode()) {
                    setEnableEditMode(true);
                    setEnableScrolling(false);
                    notify(PickerViewStateListener::editModeEnable);
                }
                break;
            case editModeDisable:
                if (isEnableEditMode()) {
                    setEnableEditMode(false);
                    setEnableScrolling(true);
                    notify(PickerViewStateListener::editModeDisable);
                }
                break;
        }
    }

    private void notify(PickerView.Callback callback) {
        for (PickerViewStateListener observer : listeners)
            callback.call(observer);
    }
}
