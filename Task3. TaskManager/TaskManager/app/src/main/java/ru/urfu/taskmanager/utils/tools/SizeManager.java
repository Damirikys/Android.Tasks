package ru.urfu.taskmanager.utils.tools;

import android.util.DisplayMetrics;

public final class SizeManager
{
    private static SizeManager instance;
    private DisplayMetrics displayMetrics;

    private SizeManager(){}

    public static int dpToPx(int dp) {
        return Math.round(dp * (instance.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void init(DisplayMetrics metrics) {
        instance = new SizeManager();
        instance.displayMetrics = metrics;
    }
}
