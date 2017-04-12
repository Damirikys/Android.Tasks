package ru.urfu.colorpicker.color_picker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import ru.urfu.colorpicker.Application;
import ru.urfu.colorpicker.utils.BitmapManager;

public abstract class AbstractPickerView extends HorizontalScrollView
{
    private LinearLayout root;
    private boolean enableScrolling = true;

    private BitmapDrawable drawable;
    private int currentColor = Color.WHITE;
    private boolean EDIT_MODE = false;
    private int cellCount = 10;

    public AbstractPickerView(Context context) {
        super(context);
    }

    public AbstractPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isEnableScrolling() {
        return enableScrolling;
    }

    public void setEnableScrolling(boolean enableScrolling) {
        this.enableScrolling = enableScrolling;
    }

    public boolean isEnableEditMode() {
        return EDIT_MODE;
    }

    protected void setEnableEditMode(boolean bool) {
        EDIT_MODE = bool;
    }

    public int getCurrentColor() {
        return currentColor;
    }

    protected void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isEnableScrolling() && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isEnableScrolling() && super.onTouchEvent(ev);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        if (root != null && root.getChildCount() == cellCount)
            init();
    }

    public void setCellCount(int cellCount) {
        removeAllViews();
        root = new LinearLayout(getContext());
        root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.setOrientation(LinearLayout.HORIZONTAL);

        this.cellCount = cellCount;
        for (int i = 0; i < cellCount; i++)
            root.addView(CellColorView.create(this));

        addView(root);
    }

    protected void calculateColors()
    {
        for (int i = 0; i < root.getChildCount(); i++)
        {
            CellColorView view = (CellColorView) root.getChildAt(i);
            int cX = (drawable.getBitmap().getWidth() / root.getChildCount()) * i;
            int cY = (drawable.getBitmap().getHeight() / 2);

            view.setCellColor(drawable.getBitmap().getPixel(cX, cY));
        }

        BitmapManager.brightness(drawable.getBitmap(), 0.8f);
        root.setBackground(drawable);
    }

    protected void init()
    {
        this.drawable = new BitmapDrawable(Application.getContext().getResources(),
                BitmapManager.getHueBitmap(getWidth(), getHeight()));

        calculateColors();
    }

    interface Callback {
        void call(PickerViewStateListener observer);
    }
}
