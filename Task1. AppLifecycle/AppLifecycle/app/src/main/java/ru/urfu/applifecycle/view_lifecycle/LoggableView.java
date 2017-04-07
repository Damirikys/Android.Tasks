package ru.urfu.applifecycle.view_lifecycle;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import ru.urfu.applifecycle.interfaces.Loggable;

public class LoggableView extends android.support.v7.widget.AppCompatTextView implements ViewLifecycleListener
{
    private static Loggable lifecycleLogger;

    public static final String TAG = "TextView";

    public LoggableView(Context context) {
        super(context);
    }

    public LoggableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void log(String body) {
        log(TAG, body);
    }

    @Override
    public void log(String tag, String body) {
        Log.d(tag, body);
        lifecycleLogger.log(tag, body);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        log("onAttachedToWindow");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        log("onDetachedFromWindow");
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        log("onMeasure");
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        log("onLayout");
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        log("dispatchDraw");
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        log("draw");
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        log("onDraw");
    }

    @Override
    public void invalidate() {
        super.invalidate();
        log("invalidate");
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        log("requestLayout");
    }

    public static void setLifecycleLogger(Loggable logger)
    {
        lifecycleLogger = logger;
    }
}
