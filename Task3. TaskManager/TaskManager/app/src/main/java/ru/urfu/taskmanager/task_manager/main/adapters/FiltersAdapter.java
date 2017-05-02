package ru.urfu.taskmanager.task_manager.main.adapters;

import android.widget.AdapterView;
import android.widget.ListAdapter;

public interface FiltersAdapter extends ListAdapter, AdapterView.OnItemClickListener{
    void update();
}
