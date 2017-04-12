package ru.urfu.colorpicker;

import android.annotation.SuppressLint;
import android.content.Context;

public class Application extends android.app.Application
{
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Application.context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
