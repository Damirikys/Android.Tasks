package ru.urfu.taskmanager;

import ru.urfu.taskmanager.libs.color_picker.recent.RecentColorsStorage;
import ru.urfu.taskmanager.view.main.filter.FiltersStorage;
import ru.urfu.taskmanager.db.DbTasks;
import ru.urfu.taskmanager.tools.SizeManager;

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
