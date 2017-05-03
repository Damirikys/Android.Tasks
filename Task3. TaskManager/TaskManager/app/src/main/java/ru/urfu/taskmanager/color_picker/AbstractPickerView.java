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

public abstract class AbstractPickerView extends HorizontalScrollView
{
    private static final int DEFAULT_CELL_COUNT = 10;
    private static final int DEFAULT_CELL_COLOR = Color.WHITE;

    private LinearLayout mRoot;

    private int mCurrentColor = DEFAULT_CELL_COLOR;
    private int mCellCount = DEFAULT_CELL_COUNT;

    private boolean mEditMode = false;
    private boolean mScrollingEnable = true;

    private float[][] mColorCache;

    public float[][] getColorCache() {
        return mColorCache;
    }

    public void setColorCache(float[][] cache) {
        mColorCache = cache;
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


    public boolean isScrollingEnable() {
        return mScrollingEnable;
    }

    public void setScrollingEnable(boolean mScrollingEnable) {
        this.mScrollingEnable = mScrollingEnable;
    }

    public boolean isEnableEditMode() {
        return mEditMode;
    }

    protected void setEnableEditMode(boolean bool) {
        mEditMode = bool;
    }


    public int getCurrentColor() {
        return mCurrentColor;
    }

    protected void setCurrentColor(int mCurrentColor) {
        this.mCurrentColor = mCurrentColor;
    }

    protected void changeColorCache(int position, float[] value) {
        mColorCache[position] = Arrays.copyOf(value, 3);
    }


    public void setCellCount(int count) {
        removeAllViews();
        mRoot = new LinearLayout(getContext());
        mRoot.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );

        mRoot.setOrientation(LinearLayout.HORIZONTAL);

        mCellCount = count;

        for (int i = 0; i < mCellCount; i++)
            mRoot.addView(CellColorView.create(this).setmPosition(i));

        addView(mRoot);
    }

    protected void init() {
        this.setBackgroundColor(Color.BLACK);
        mRoot.setBackground(getHueGradientDrawable());
        calculateColors();
    }

    protected void calculateColors() {
        boolean restoreCache = (mColorCache != null && mColorCache.length == mCellCount);
        if (!restoreCache) mColorCache = new float[mCellCount][3];

        float[] hsv = new float[]{0f, 1f, 1f};

        for (int i = 0; i < mRoot.getChildCount(); i++) {
            CellColorView view = (CellColorView) mRoot.getChildAt(i);

            float offset = ((getWidth() / mRoot.getChildCount()) * i);
            hsv[0] = (360 * (offset + CellColorView.CELL_MARGIN)) / getWidth();
            view.setmDefaultColor(hsv);

            if (restoreCache) {
                view.setCurrentColor(mColorCache[i]);
            } else {
                mColorCache[i] = Arrays.copyOf(hsv, 3);
            }
        }
    }


    public float[] getHsvBorders(CellColorView cellColorView) {
        float[] borders = new float[2];
        float[] defaultColorHsv = new float[3];
        Color.colorToHSV(cellColorView.getmDefaultColor(), defaultColorHsv);

        int position = cellColorView.getmPosition();

        if (position == 0) {

            CellColorView next = (CellColorView) mRoot.getChildAt(cellColorView.getmPosition() + 1);
            float[] hsv_next = new float[3];
            Color.colorToHSV(next.getmDefaultColor(), hsv_next);
            borders[0] = 0f;
            borders[1] = defaultColorHsv[0] + ((hsv_next[0] - defaultColorHsv[0]) / 2);

        } else if (position == mCellCount - 1) {

            CellColorView prev = (CellColorView) mRoot.getChildAt(cellColorView.getmPosition() - 1);
            float[] hsv_prev = new float[3];
            Color.colorToHSV(prev.getmDefaultColor(), hsv_prev);
            borders[0] = hsv_prev[0] + ((defaultColorHsv[0] - hsv_prev[0]) / 2);
            borders[1] = 360f;

        } else {

            CellColorView prev = (CellColorView) mRoot.getChildAt(cellColorView.getmPosition() - 1);
            CellColorView next = (CellColorView) mRoot.getChildAt(cellColorView.getmPosition() + 1);

            float[] hsv_prev = new float[3];
            Color.colorToHSV(prev.getmDefaultColor(), hsv_prev);
            float[] hsv_next = new float[3];
            Color.colorToHSV(next.getmDefaultColor(), hsv_next);

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
        return isScrollingEnable() && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isScrollingEnable() && super.onTouchEvent(ev);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mRoot != null && mRoot.getChildCount() == mCellCount)
            init();
    }

    interface Callback {
        void call(PickerViewStateListener observer);
    }
}
