package ru.urfu.applifecycle.view_lifecycle;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.urfu.applifecycle.interfaces.Loggable;
import ru.urfu.applifecycle.utils.TimeUtils;

public class LoggableGroupView extends RelativeLayout implements ViewLifecycleListener
{
    private static Loggable lifecycleLogger;

    public static final String TAG = "RelativeLayout";

    public LoggableGroupView(Context context) {
        super(context);
    }

    public LoggableGroupView(Context context, @Nullable AttributeSet attrs) {
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
    public void addView(View child) {
        log("addView");
        super.addView(child);
    }

    @Override
    public void removeView(View view) {
        log("removeView");
        super.removeView(view);
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
