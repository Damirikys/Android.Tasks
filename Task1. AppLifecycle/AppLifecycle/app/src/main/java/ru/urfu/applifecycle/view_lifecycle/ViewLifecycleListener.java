package ru.urfu.applifecycle.view_lifecycle;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import ru.urfu.applifecycle.interfaces.Loggable;
import ru.urfu.applifecycle.utils.TimeUtils;

public interface ViewLifecycleListener extends Loggable
{
    void onAttachedToWindow();
    void onDetachedFromWindow();
    void onMeasure(int widthMeasureSpec, int heightMeasureSpec);
    void onLayout(boolean changed, int l, int t, int r, int b);
    void dispatchDraw(Canvas canvas);
    void draw(Canvas canvas);
    void onDraw(Canvas canvas);
    void invalidate();
    void requestLayout();
}
