package ru.urfu.taskmanager.task_manager.fragments.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.db.DbTasks;
import ru.urfu.taskmanager.utils.db.DbTasksFilter;
import ru.urfu.taskmanager.utils.tools.TimeUtils;

public class TasksListAdapter extends AbstractTaskListAdapter
{
    private final Context mContext;
    private final DbTasksFilter defaultFilter;
    private final DbTasks mDatabase;

    public TasksListAdapter(Context context, DbTasksFilter tasksFilter) {
        super(context, LAYOUT, null, FROM, TO, 0);
        this.mContext = context;
        this.defaultFilter = tasksFilter;
        this.mDatabase = DbTasks.getInstance();
        updateData(mDatabase.getCursor(defaultFilter));
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
        TaskEntry entry = mDatabase.getCurrentEntryFromCursor(cursor);
        TaskEntry prev = (cursor.moveToPrevious())
                ? mDatabase.getCurrentEntryFromCursor(cursor)
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

    private ViewHolder attachHeader(ViewHolder holder, TaskEntry entry, TaskEntry prev) {
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
        if (System.currentTimeMillis() > entry.getTtlTimestamp() & defaultFilter.getType() == DbTasksFilter.ACTIVE_TASK) {
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
            switch (defaultFilter.getType()) {
                case DbTasksFilter.ACTIVE_TASK:
                    return ACTIVE_DAYS[num];
                case DbTasksFilter.COMPLETED_TASK:
                    return COMPLETED_DAYS[num];
            }
        }

        return TimeUtils.format(entryDate);
    }

    public void updateData(Cursor... cursor) {
        if (cursor.length == 0) {
            updateCursor(mDatabase.getCursor(defaultFilter));
        } else {
            updateCursor(cursor[0]);
        }
    }

    private void updateCursor(Cursor cursor) {
        changeCursor(cursor);
    }

    public int getDataType() {
        return defaultFilter.getType();
    }

    private static class ViewHolder {
        View layout;
        View header;
        TextView header_text;
        TextView title;
        TextView description;
        TextView ttl;
    }
}
