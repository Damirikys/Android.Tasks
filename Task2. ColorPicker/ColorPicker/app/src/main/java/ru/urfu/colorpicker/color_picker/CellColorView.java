package ru.urfu.colorpicker.color_picker;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Arrays;

import ru.urfu.colorpicker.color_picker.states.Action;
import ru.urfu.colorpicker.utils.SizeManager;

public class CellColorView extends View implements View.OnTouchListener
{
    private static LinearLayout.LayoutParams defaultParams;
    private static LinearLayout.LayoutParams scaledParams;
    public static final int CELL_SIZE = SizeManager.dpToPx(65);

    static {
        int margin = (int) (CELL_SIZE * 0.25);
        defaultParams = new LinearLayout.LayoutParams(CELL_SIZE, CELL_SIZE);
        defaultParams.setMargins(margin, margin, margin, margin);
        scaledParams = new LinearLayout.LayoutParams(CELL_SIZE + CELL_SIZE /2, CELL_SIZE + CELL_SIZE /2);
    }

    private GestureDetector gestureDetector;
    private GestureListener gestureListener;
    private PickerView parent;
    private int position;

    private int defaultColor;
    private float[] defaultHsvColor = new float[3];
    private float[] currentColor = new float[3];
    private float[] colorBuffer = new float[3];

    private float LEFT_HSV_BORDER;
    private float RIGHT_HSV_BORDER;


    private CellColorView(Context context) {
        super(context);
    }

    private CellColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private CellColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public CellColorView setDefaultColor(float[] hsvDefaultColor)
    {
        this.defaultColor = Color.HSVToColor(hsvDefaultColor);
        this.defaultHsvColor = Arrays.copyOf(hsvDefaultColor, 3);

        return setCurrentColor(defaultHsvColor);
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public CellColorView setCurrentColor(float[] newColor)
    {
        this.currentColor = Arrays.copyOf(newColor, 3);
        this.colorBuffer = Arrays.copyOf(newColor, 3);

        setBackgroundColor(Color.HSVToColor(newColor));
        return this;
    }

    private CellColorView setDefaultLayoutParams(ViewGroup.LayoutParams params)
    {
        setLayoutParams(params);
        return this;
    }

    public CellColorView setPosition(int position) {
        this.position = position;
        return this;
    }

    public int getPosition() {
        return position;
    }

    private CellColorView build(AbstractPickerView view)
    {
        this.parent = (PickerView) view;
        this.setOnTouchListener(this);

        gestureDetector = new GestureDetector(
                getContext(), gestureListener = new GestureListener()
        );

        return this;
    }

    private void offsetColor(float x, float y)
    {
        float fromX = gestureListener.tapX;
        float fromY = gestureListener.tapY ;

        float distanceX = (x - fromX);
        float distanceY = ((y - fromY) * 0.005f);

        float[] hsvValue = Arrays.copyOf(currentColor, 3);

        hsvValue[0] = hsvValue[0] + (distanceX / 10);
        hsvValue[1] = hsvValue[1] + distanceY;
        hsvValue[2] = hsvValue[2] - distanceY;

        if (hsvValue[0] < LEFT_HSV_BORDER || hsvValue[0] > RIGHT_HSV_BORDER)
        {
            parent.notifySubscribers(Action.theBoundaryIsReached);
            hsvValue[0] = (hsvValue[0] < LEFT_HSV_BORDER) ? LEFT_HSV_BORDER : RIGHT_HSV_BORDER;
        }

        colorBuffer = hsvValue;
        setBackgroundColor(Color.HSVToColor(hsvValue));
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if (parent.isEnableEditMode())
                {
                    if (gestureListener.newTouch)
                    {
                        gestureListener.tapX = event.getX();
                        gestureListener.tapY = event.getY();
                        gestureListener.newTouch = false;
                    }

                    offsetColor(event.getX(), event.getY());
                } break;
            case MotionEvent.ACTION_UP:
                gestureListener.newTouch = true;
                parent.notifySubscribers(Action.editModeDisable);
                setCurrentColor(colorBuffer);
                parent.changeColorCache(position, colorBuffer);
                setLayoutParams(defaultParams);
                invalidate();
                break;
        }

        return false;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        boolean newTouch = true;
        float tapX, tapY;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            float[] borders = parent.getHsvBorders(CellColorView.this);
            LEFT_HSV_BORDER = borders[0];
            RIGHT_HSV_BORDER = borders[1];

            parent.notifySubscribers(Action.editModeEnable);
            CellColorView.this.setLayoutParams(scaledParams);
            CellColorView.this.invalidate();
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            setDefaultColor(defaultHsvColor);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            parent.setCurrentColor(Color.HSVToColor(currentColor));
            return super.onSingleTapConfirmed(e);
        }
    }

    public static CellColorView create(AbstractPickerView pickerView)
    {
        return new CellColorView(pickerView.getContext())
                .setDefaultLayoutParams(defaultParams)
                .build(pickerView);
    }
}
