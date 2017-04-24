package ru.urfu.taskmanager.utils.tools;

import android.util.DisplayMetrics;

import ru.urfu.taskmanager.Application;

public class SizeManager
{
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = Application.getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
