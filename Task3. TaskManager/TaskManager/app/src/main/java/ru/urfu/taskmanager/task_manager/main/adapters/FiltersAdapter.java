package ru.urfu.taskmanager.task_manager.main.adapters;

import android.widget.AdapterView;
import android.widget.ListAdapter;

import ru.urfu.taskmanager.utils.db.TasksFilter;

public interface FiltersAdapter extends ListAdapter, AdapterView.OnItemClickListener
{
    void addItem(String name, TasksFilter.Builder builder);

    void update();
}
