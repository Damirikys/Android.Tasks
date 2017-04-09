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
        log("onAttachedToWindow");
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        log("onDetachedFromWindow");
        super.onDetachedFromWindow();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        log("onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        log("onLayout");
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        log("dispatchDraw");
        super.dispatchDraw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        log("draw");
        super.draw(canvas);
    }

    @Override
    public void onDraw(Canvas canvas) {
        log("onDraw");
        super.onDraw(canvas);
    }

    @Override
    public void invalidate() {
        log("invalidate");
        super.invalidate();
    }

    @Override
    public void requestLayout() {
        log("requestLayout");
        super.requestLayout();
    }

    public static void setLifecycleLogger(Loggable logger)
    {
        lifecycleLogger = logger;
    }
}
