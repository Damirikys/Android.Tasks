package ru.urfu.taskmanager;

import android.annotation.SuppressLint;
import android.content.Context;

import ru.urfu.taskmanager.color_picker.recent.RecentColorsStorage;
import ru.urfu.taskmanager.task_manager.main.filter.FiltersStorage;
import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.utils.tools.SizeManager;

public class Application extends android.app.Application
{
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SizeManager.init(getResources().getDisplayMetrics());
        TasksDatabase.init(getApplicationContext());
        RecentColorsStorage.init(getApplicationContext());
        FiltersStorage.init(getApplicationContext());
    }

    public static Context getContext() {
        return context;
    }
}
