package ru.urfu.taskmanager.interfaces;

import android.support.annotation.UiThread;

@UiThread
public interface Progressive extends Showable, ActivityWindow
{
    void startProgressIndicator(int max);

    void setProgressIndicatorValue(int value);

    void stopProgressIndicator();

    void showProgress();

    void showProgress(String title, String message);

    void hideProgress();
}
