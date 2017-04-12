package ru.urfu.colorpicker.utils;

import android.util.DisplayMetrics;

import ru.urfu.colorpicker.Application;

public class SizeManager
{
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = Application.getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
