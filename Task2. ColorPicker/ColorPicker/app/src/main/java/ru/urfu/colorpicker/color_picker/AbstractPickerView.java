package ru.urfu.colorpicker.color_picker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public abstract class AbstractPickerView extends HorizontalScrollView
{
    private LinearLayout root;
    private boolean enableScrolling = true;

    private PaintDrawable drawable;
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
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );

        root.setOrientation(LinearLayout.HORIZONTAL);

        this.cellCount = cellCount;
        for (int i = 0; i < cellCount; i++)
            root.addView(CellColorView.create(this));

        addView(root);
    }

    protected void calculateColors()
    {
        float[] hsv = new float[] {0f, 1f, 1f};

        for (int i = 0; i < root.getChildCount(); i++)
        {
            float offset = ((getWidth() / root.getChildCount()) * i);
            hsv[0] = 360 * offset / getWidth();

            CellColorView view = (CellColorView) root.getChildAt(i);
            view.setCellColor(Color.HSVToColor(hsv));
        }
    }

    protected void init()
    {
        this.setBackgroundColor(Color.BLACK);
        this.drawable = getHueGradientDrawable();
        this.drawable.setColorFilter(new ColorFilter());
        root.setBackground(drawable);

        calculateColors();
    }

    interface Callback {
        void call(PickerViewStateListener observer);
    }

    private PaintDrawable getHueGradientDrawable() {
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, width, height,
                        new int[] {
                                0xFFFF0D00,
                                0xFFFBFF00,
                                0xFF04FF00,
                                0xFF00FBFF,
                                0xFF0800FF,
                                0xFFFF00FF,
                                0xFFFF0004
                        },
                        null, Shader.TileMode.CLAMP);
            }
        };

        PaintDrawable paint = new PaintDrawable();
        paint.setShape(new RectShape());
        paint.setShaderFactory(shaderFactory);
        paint.setAlpha(220);

        return paint;
    }
}
