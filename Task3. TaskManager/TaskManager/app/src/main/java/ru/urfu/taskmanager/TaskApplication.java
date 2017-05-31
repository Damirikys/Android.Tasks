package ru.urfu.taskmanager;

import ru.urfu.taskmanager.color_picker.recent.RecentColorsStorage;
import ru.urfu.taskmanager.task_manager.main.filter.FiltersStorage;
import ru.urfu.taskmanager.data.db.DbTasks;
import ru.urfu.taskmanager.utils.tools.SizeManager;

public class TaskApplication extends android.app.Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        SizeManager.init(getResources().getDisplayMetrics());
        DbTasks.init(getApplicationContext());
        RecentColorsStorage.init(getApplicationContext());
        FiltersStorage.init(getApplicationContext());
    }
}
