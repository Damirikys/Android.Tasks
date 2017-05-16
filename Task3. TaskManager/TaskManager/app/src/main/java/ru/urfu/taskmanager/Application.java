package ru.urfu.taskmanager;

import android.content.BroadcastReceiver;

import ru.urfu.taskmanager.color_picker.recent.RecentColorsStorage;
import ru.urfu.taskmanager.task_manager.main.filter.FiltersStorage;
import ru.urfu.taskmanager.data.db.DbTasks;
import ru.urfu.taskmanager.utils.tools.SizeManager;

public class Application extends android.app.Application
{
    private BroadcastReceiver apiSyncReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        SizeManager.init(getResources().getDisplayMetrics());
        DbTasks.init(getApplicationContext());
        RecentColorsStorage.init(getApplicationContext());
        FiltersStorage.init(getApplicationContext());
    }
}
