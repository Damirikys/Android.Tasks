package ru.urfu.taskmanager;

import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.utils.tools.SizeManager;

public class Application extends android.app.Application
{
    @Override
    public void onCreate() {
        super.onCreate();
        SizeManager.init(getResources().getDisplayMetrics());
        TasksDatabase.init(getApplicationContext());
    }
}
