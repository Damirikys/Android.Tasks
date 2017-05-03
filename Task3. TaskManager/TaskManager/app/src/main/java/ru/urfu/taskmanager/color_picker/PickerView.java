package ru.urfu.taskmanager.color_picker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import ru.urfu.taskmanager.color_picker.listeners.PickerViewStateListener;
import ru.urfu.taskmanager.color_picker.states.Action;


public class PickerView extends AbstractPickerView
{
    protected List<PickerViewStateListener> mListeners = new ArrayList<>();

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
    public void setCurrentColor(int mCurrentColor) {
        super.setCurrentColor(mCurrentColor);
        notifySubscribers(Action.onColorChange);
    }


    public void subscribe(PickerViewStateListener listener) {
        mListeners.add(listener);
    }

    public void unsubscribe(PickerViewStateListener listener) {
        mListeners.remove(listener);
    }


    protected void notifySubscribers(Action state) {
        switch (state) {
            case onColorChange:
                notify(observer -> observer.onColorChanged(getCurrentColor()));
                break;
            case editModeEnable:
                if (!isEnableEditMode()) {
                    setEnableEditMode(true);
                    setScrollingEnable(false);
                    notify(PickerViewStateListener::editModeEnable);
                }
                break;
            case editModeDisable:
                if (isEnableEditMode()) {
                    setEnableEditMode(false);
                    setScrollingEnable(true);
                    notify(PickerViewStateListener::editModeDisable);
                }
                break;
            case theBoundaryIsReached:
                notify(PickerViewStateListener::theBoundaryIsReached);
                break;
        }
    }

    private void notify(PickerView.Callback callback) {
        for (PickerViewStateListener observer : mListeners)
            callback.call(observer);
    }
}
