package ru.urfu.taskmanager.task_manager.main.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.main.filter.FiltersStorage;
import ru.urfu.taskmanager.utils.db.TasksFilter;


public class SavedFiltersAdapter extends BaseAdapter implements FiltersAdapter {
    private class ViewHolder {
        TextView title;

        ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    private Context context;


    private OnFilterSelected onFilterSelected;
    private FiltersStorage storage = FiltersStorage.getStorage();
    private List<String> keys;


    public SavedFiltersAdapter(@NonNull Context context, @Nullable OnFilterSelected onFilterSelected) {
        this.context = context;
        this.onFilterSelected = onFilterSelected;
        update();
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public Object getItem(int position) {
        return storage.getBuilder(keys.get(position));
    }

    @Override
    public long getItemId(int position) {
        return keys.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.saved_filters_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(keys.get(position));
        return convertView;
    }

    @Override
    public void addItem(String name, TasksFilter.Builder builder) {
        storage.putBuilder(name, builder);
        update();
    }

    @Override
    public void update() {
        keys = new ArrayList<>(storage.getBuilders().keySet());
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onFilterSelected != null) {
            TasksFilter.Builder builder = storage.getBuilder(keys.get(position));
            onFilterSelected.onFilterSelected(builder);
        }
    }

    public interface OnFilterSelected {
        void onFilterSelected(TasksFilter.Builder builder);
    }
}
