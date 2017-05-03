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


public class SavedFiltersAdapter extends BaseAdapter implements FiltersAdapter
{
    private class ViewHolder
    {
        TextView title;

        ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    private Context mContext;


    private OnFilterSelected mFilterSelectedListener;
    private FiltersStorage mStorage = FiltersStorage.getStorage();
    private List<String> mKeys;


    public SavedFiltersAdapter(@NonNull Context context, @Nullable OnFilterSelected onFilterSelected) {
        this.mContext = context;
        this.mFilterSelectedListener = onFilterSelected;
        update();
    }

    @Override
    public int getCount() {
        return mKeys.size();
    }

    @Override
    public Object getItem(int position) {
        return mStorage.getBuilder(mKeys.get(position));
    }

    @Override
    public long getItemId(int position) {
        return mKeys.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.saved_filters_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(mKeys.get(position));
        return convertView;
    }

    @Override
    public void addItem(String name, TasksFilter.Builder builder) {
        mStorage.putBuilder(name, builder);
        update();
    }

    @Override
    public void update() {
        mKeys = new ArrayList<>(mStorage.getBuilders().keySet());
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mFilterSelectedListener != null) {
            TasksFilter.Builder builder = mStorage.getBuilder(mKeys.get(position));
            mFilterSelectedListener.onFilterSelected(builder);
        }
    }

    public interface OnFilterSelected {
        void onFilterSelected(TasksFilter.Builder builder);
    }
}
