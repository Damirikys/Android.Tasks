package ru.urfu.taskmanager.task_manager.main.adapters;

import android.widget.AdapterView;
import android.widget.ListAdapter;

import ru.urfu.taskmanager.data.db.DbTasksFilter;

public interface FiltersAdapter extends ListAdapter, AdapterView.OnItemClickListener
{
    void addItem(String name, DbTasksFilter.Builder builder);

    void update();
}
