package ru.urfu.taskmanager.color_picker;

import android.content.Context;
import android.graphics.Color;
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

import java.util.Arrays;

import ru.urfu.taskmanager.color_picker.listeners.PickerViewStateListener;

public abstract class AbstractPickerView extends HorizontalScrollView {
    private static final int DEFAULT_CELL_COUNT = 10;
    private static final int DEFAULT_CELL_COLOR = Color.WHITE;

    private LinearLayout root;

    private int currentColor = DEFAULT_CELL_COLOR;
    private int cellCount = DEFAULT_CELL_COUNT;

    private boolean EDIT_MODE = false;
    private boolean enableScrolling = true;

    private float[][] colorCache;

    public float[][] getColorCache() {
        return colorCache;
    }

    public void setColorCache(float[][] cache) {
        colorCache = cache;
    }

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

    protected void changeColorCache(int position, float[] value) {
        colorCache[position] = Arrays.copyOf(value, 3);
    }


    public void setCellCount(int count) {
        removeAllViews();
        root = new LinearLayout(getContext());
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );

        root.setOrientation(LinearLayout.HORIZONTAL);

        cellCount = count;

        for (int i = 0; i < cellCount; i++)
            root.addView(CellColorView.create(this).setPosition(i));

        addView(root);
    }

    protected void init() {
        this.setBackgroundColor(Color.BLACK);
        root.setBackground(getHueGradientDrawable());
        calculateColors();
    }

    protected void calculateColors() {
        boolean restoreCache = (colorCache != null && colorCache.length == cellCount);
        System.out.println("RESTORE CACHE: " + restoreCache);
        if (!restoreCache) colorCache = new float[cellCount][3];

        float[] hsv = new float[]{0f, 1f, 1f};

        for (int i = 0; i < root.getChildCount(); i++) {
            CellColorView view = (CellColorView) root.getChildAt(i);

            float offset = ((getWidth() / root.getChildCount()) * i);
            hsv[0] = (360 * (offset + CellColorView.CELL_MARGIN)) / getWidth();
            view.setDefaultColor(hsv);

            if (restoreCache) {
                view.setCurrentColor(colorCache[i]);
            } else {
                colorCache[i] = Arrays.copyOf(hsv, 3);
            }
        }

        System.out.println("COLOR CACHE: " + (colorCache != null && colorCache.length == cellCount));
    }


    public float[] getHsvBorders(CellColorView cellColorView) {
        float[] borders = new float[2];
        float[] defaultColorHsv = new float[3];
        Color.colorToHSV(cellColorView.getDefaultColor(), defaultColorHsv);

        int position = cellColorView.getPosition();

        if (position == 0) {

            CellColorView next = (CellColorView) root.getChildAt(cellColorView.getPosition() + 1);
            float[] hsv_next = new float[3];
            Color.colorToHSV(next.getDefaultColor(), hsv_next);
            borders[0] = 0f;
            borders[1] = defaultColorHsv[0] + ((hsv_next[0] - defaultColorHsv[0]) / 2);

        } else if (position == cellCount - 1) {

            CellColorView prev = (CellColorView) root.getChildAt(cellColorView.getPosition() - 1);
            float[] hsv_prev = new float[3];
            Color.colorToHSV(prev.getDefaultColor(), hsv_prev);
            borders[0] = hsv_prev[0] + ((defaultColorHsv[0] - hsv_prev[0]) / 2);
            borders[1] = 360f;

        } else {

            CellColorView prev = (CellColorView) root.getChildAt(cellColorView.getPosition() - 1);
            CellColorView next = (CellColorView) root.getChildAt(cellColorView.getPosition() + 1);

            float[] hsv_prev = new float[3];
            Color.colorToHSV(prev.getDefaultColor(), hsv_prev);
            float[] hsv_next = new float[3];
            Color.colorToHSV(next.getDefaultColor(), hsv_next);

            borders[0] = hsv_prev[0] + ((defaultColorHsv[0] - hsv_prev[0]) / 2);
            borders[1] = defaultColorHsv[0] + ((hsv_next[0] - defaultColorHsv[0]) / 2);
        }

        return borders;
    }

    private PaintDrawable getHueGradientDrawable() {
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(0, 0, width, height,
                        new int[]{
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
        paint.setAlpha(240);

        return paint;
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (root != null && root.getChildCount() == cellCount)
            init();
    }

    interface Callback {
        void call(PickerViewStateListener observer);
    }
}
