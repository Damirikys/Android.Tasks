package ru.urfu.taskmanager.libs.color_picker;

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

import ru.urfu.taskmanager.libs.color_picker.states.Action;
import ru.urfu.taskmanager.tools.SizeManager;

import static ru.urfu.taskmanager.libs.color_picker.AbstractPickerView.HSV_ARRAY_LENGTH;

@SuppressWarnings("unused")
public class CellColorView extends View implements View.OnTouchListener
{
    private static final int CELL_SIZE = SizeManager.dpToPx(45);
    public static final int CELL_MARGIN = (int) (CELL_SIZE * 0.25);

    private static LinearLayout.LayoutParams sDefaultParams;
    private static LinearLayout.LayoutParams sScaledParams;

    static {
        sDefaultParams = new LinearLayout.LayoutParams(CELL_SIZE, CELL_SIZE);
        sDefaultParams.setMargins(CELL_MARGIN, CELL_MARGIN, CELL_MARGIN, CELL_MARGIN);
        sScaledParams = new LinearLayout.LayoutParams(CELL_SIZE + CELL_SIZE / 2, CELL_SIZE + CELL_SIZE / 2);
    }

    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private PickerView mParent;
    private int mPosition;

    private int mDefaultColor;
    private float[] mDefaultHsvColor = new float[HSV_ARRAY_LENGTH];
    private float[] mCurrentColor = new float[HSV_ARRAY_LENGTH];
    private float[] mColorBuffer = new float[HSV_ARRAY_LENGTH];

    private float mLeftHsvBorder;
    private float mRightHsvBorder;


    private CellColorView(Context context) {
        super(context);
    }

    private CellColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private CellColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static CellColorView create(AbstractPickerView pickerView) {
        return new CellColorView(pickerView.getContext()).setDefaultLayoutParams(sDefaultParams).build(pickerView);
    }

    public int getmDefaultColor() {
        return mDefaultColor;
    }

    public CellColorView setmDefaultColor(float[] hsvDefaultColor) {
        this.mDefaultColor = Color.HSVToColor(hsvDefaultColor);
        this.mDefaultHsvColor = Arrays.copyOf(hsvDefaultColor, HSV_ARRAY_LENGTH);

        return setCurrentColor(mDefaultHsvColor);
    }

    public CellColorView setCurrentColor(float[] newColor) {
        this.mCurrentColor = Arrays.copyOf(newColor, HSV_ARRAY_LENGTH);
        this.mColorBuffer = Arrays.copyOf(newColor, HSV_ARRAY_LENGTH);

        setBackgroundColor(Color.HSVToColor(newColor));
        return this;
    }

    private CellColorView setDefaultLayoutParams(ViewGroup.LayoutParams params) {
        setLayoutParams(params);
        return this;
    }

    public int getmPosition() {
        return mPosition;
    }

    public CellColorView setmPosition(int mPosition) {
        this.mPosition = mPosition;
        return this;
    }

    private CellColorView build(AbstractPickerView view) {
        this.mParent = (PickerView) view;
        this.setOnTouchListener(this);

        mGestureDetector = new GestureDetector(getContext(), mGestureListener = new GestureListener());

        return this;
    }

    private void offsetColor(float x, float y) {
        float fromX = mGestureListener.tapX;
        float fromY = mGestureListener.tapY;

        float distanceX = (x - fromX);
        float distanceY = ((y - fromY) * 0.005f);

        float[] hsvValue = Arrays.copyOf(mCurrentColor, HSV_ARRAY_LENGTH);

        hsvValue[0] = hsvValue[0] + (distanceX / 10);
        hsvValue[1] = hsvValue[1] + distanceY;
        hsvValue[2] = hsvValue[2] - distanceY;

        if (hsvValue[0] < mLeftHsvBorder || hsvValue[0] > mRightHsvBorder) {
            mParent.notifySubscribers(Action.theBoundaryIsReached);
            hsvValue[0] = (hsvValue[0] < mLeftHsvBorder) ? mLeftHsvBorder : mRightHsvBorder;
        }

        mColorBuffer = hsvValue;
        setBackgroundColor(Color.HSVToColor(hsvValue));
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (mParent.isEnableEditMode()) {
                    if (mGestureListener.newTouch) {
                        mGestureListener.tapX = event.getX();
                        mGestureListener.tapY = event.getY();
                        mGestureListener.newTouch = false;
                    }

                    offsetColor(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                mGestureListener.newTouch = true;
                mParent.notifySubscribers(Action.editModeDisable);
                setCurrentColor(mColorBuffer);
                mParent.changeColorCache(mPosition, mColorBuffer);
                setLayoutParams(sDefaultParams);
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
            float[] borders = mParent.getHsvBorders(CellColorView.this);
            mLeftHsvBorder = borders[0];
            mRightHsvBorder = borders[1];

            mParent.notifySubscribers(Action.editModeEnable);
            CellColorView.this.setLayoutParams(sScaledParams);
            CellColorView.this.invalidate();
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            setmDefaultColor(mDefaultHsvColor);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            mParent.setCurrentColor(Color.HSVToColor(mCurrentColor));
            return super.onSingleTapConfirmed(e);
        }
    }
}
