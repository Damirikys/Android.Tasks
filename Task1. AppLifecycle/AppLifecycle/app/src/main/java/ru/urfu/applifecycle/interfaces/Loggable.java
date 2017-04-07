package ru.urfu.applifecycle.interfaces;

import android.util.Log;
import android.view.View;

import ru.urfu.applifecycle.utils.TimeUtils;

public interface Loggable
{
    void log(String tag, String body);

    static void logWithTime(String classTag, String body)
    {
        String tag = TimeUtils.getTimeRepresentationFromUnix(
                System.currentTimeMillis()) + " | " + classTag;

        Log.d(tag, body);
    }
}
