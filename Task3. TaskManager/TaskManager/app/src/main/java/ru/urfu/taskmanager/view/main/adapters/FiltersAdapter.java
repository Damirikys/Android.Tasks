package ru.urfu.taskmanager.view.main.adapters;

import android.widget.AdapterView;
import android.widget.ListAdapter;

import ru.urfu.taskmanager.db.filter.DbTasksFilter;

@SuppressWarnings("unused")
public interface FiltersAdapter extends ListAdapter, AdapterView.OnItemClickListener
{
    void addItem(String name, DbTasksFilter.Builder builder);

    void update();
}
