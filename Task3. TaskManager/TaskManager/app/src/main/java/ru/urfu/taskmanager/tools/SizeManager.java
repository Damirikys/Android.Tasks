package ru.urfu.taskmanager.tools;

import android.util.DisplayMetrics;

public final class SizeManager
{
    private static SizeManager sInstance;
    private DisplayMetrics mDisplayMetrics;

    private SizeManager() {
    }

    public static int dpToPx(int dp) {
        return Math.round(dp * (sInstance.mDisplayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void init(DisplayMetrics metrics) {
        sInstance = new SizeManager();
        sInstance.mDisplayMetrics = metrics;
    }
}
