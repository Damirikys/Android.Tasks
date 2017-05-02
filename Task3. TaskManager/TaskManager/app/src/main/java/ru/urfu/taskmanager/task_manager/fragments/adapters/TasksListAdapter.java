package ru.urfu.taskmanager.task_manager.fragments.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.db.TasksFilter;
import ru.urfu.taskmanager.utils.tools.TimeUtils;

public class TasksListAdapter extends AbstractTaskListAdapter
{
    private final TasksDatabase database;
    private TasksFilter tasksFilter;

    public TasksListAdapter(Context context, TasksFilter tasksFilter) {
        super(context, LAYOUT, null, FROM, TO, 0);
        this.database = TasksDatabase.getInstance();
        this.tasksFilter = tasksFilter;
        updateData();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(LAYOUT, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.layout = view.findViewById(R.id.task_layout);
        holder.header = view.findViewById(R.id.task_header);
        holder.header_text = (TextView) view.findViewById(R.id.header_text);
        holder.title = (TextView) view.findViewById(R.id.task_item_title);
        holder.description = (TextView) view.findViewById(R.id.task_item_description);
        holder.ttl = (TextView) view.findViewById(R.id.task_item_deadline);

        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TaskEntry entry = database.getCurrentEntryFromCursor(cursor);
        TaskEntry prev = (cursor.moveToPrevious())
                ? database.getCurrentEntryFromCursor(cursor)
                : null;

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(entry.getColorInt());
        gd.setCornerRadius(100f);

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.title.setText(entry.getTitle());
        holder.description.setText(entry.getDescription());
        holder.ttl.setText(TimeUtils.getHoursAndMinutesFromUnix(entry.getTtlTimestamp()).toString());
        holder.ttl.setBackground(gd);
        attachHeader(holder, entry, prev);
    }

    private ViewHolder attachHeader(ViewHolder holder, TaskEntry entry, TaskEntry prev)
    {
        String entryTitle = getTitleFromEntry(entry);
        if (entryTitle.equals(OVERDUE))
            holder.layout.setAlpha(0.4f);
        else holder.layout.setAlpha(1f);

        if (prev != null) {
            String prevTitle = getTitleFromEntry(prev);

            if (!entryTitle.equals(prevTitle)) {
                holder.header_text.setText(entryTitle);
                holder.header.setVisibility(View.VISIBLE);
            } else {
                holder.header.setVisibility(View.GONE);
            }
        } else {
            holder.header_text.setText(entryTitle);
            holder.header.setVisibility(View.VISIBLE);
        }

        return holder;
    }

    private String getTitleFromEntry(TaskEntry entry) {
        if (System.currentTimeMillis() > entry.getTtlTimestamp() & tasksFilter.getType() == TasksFilter.ACTIVE_TASK) {
            return OVERDUE;
        } else {
            Calendar entryDate = Calendar.getInstance();
            entryDate.setTimeInMillis(entry.getTtlTimestamp());
            int diffDay = Math.abs(entryDate.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
            return getHeaderTitleByNum(diffDay, entryDate);
        }
    }

    private String getHeaderTitleByNum(int num, Calendar entryDate) {
        if (num < 3) {
            switch (tasksFilter.getType()) {
                case TasksFilter.ACTIVE_TASK:
                    return ACTIVE_DAYS[num];
                case TasksFilter.COMPLETED_TASK:
                    return COMPLETED_DAYS[num];
            }
        }

        return TimeUtils.format(entryDate);
    }

    public void updateData(TasksFilter.Builder builder) {
        if (builder.isDefault()) {
            updateData();
        } else {
            changeCursor(database.getCursor(
                    tasksFilter = builder.setType(tasksFilter.getType())
                            .build()
            ));
        }
    }

    private void updateData() {
        changeCursor(database.getCursor(tasksFilter));
    }

    private static class ViewHolder
    {
        View layout;
        View header;
        TextView header_text;
        TextView title;
        TextView description;
        TextView ttl;
    }
}
