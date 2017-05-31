package ru.urfu.taskmanager.task_manager.main.fragments.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.data.db.DbFilter;
import ru.urfu.taskmanager.data.db.DbTasks;
import ru.urfu.taskmanager.data.db.DbTasksFilter;
import ru.urfu.taskmanager.data.db.SimpleDatabase;
import ru.urfu.taskmanager.task_manager.main.fragments.helper.ItemTouchHelperViewHolder;
import ru.urfu.taskmanager.task_manager.main.fragments.view.TaskListFragment;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.tools.TimeUtils;

public class TasksListAdapter extends AbstractTaskListAdapter<TasksListAdapter.ViewHolder>
{
    private final TaskListFragment mFragment;
    private final SimpleDatabase<TaskEntry> mDatabase;

    private DbFilter mDefaultFilter;

    public TasksListAdapter(TaskListFragment fragment, DbTasksFilter filter) {
        super(null);
        this.mFragment = fragment;
        this.mDefaultFilter = filter;
        this.mDatabase = DbTasks.getInstance();
        changeCursor(mDatabase.getCursor(mDefaultFilter));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        TaskEntry entry = mDatabase.getCurrentEntryFromCursor(cursor);
        TaskEntry prev = (cursor.moveToPrevious())
                ? mDatabase.getCurrentEntryFromCursor(cursor)
                : null;

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(entry.getColorInt());
        gd.setCornerRadius(100f);

        viewHolder.title.setText(entry.getTitle());
        viewHolder.description.setText(entry.getDescription());
        viewHolder.ttl.setText(TimeUtils.getHoursAndMinutesFromUnix(entry.getTtlTimestamp()).toString());
        viewHolder.ttl.setBackground(gd);

        ViewCompat.setTransitionName(viewHolder.ttl, String.valueOf(cursor.getPosition()) + mDefaultFilter.getType());
        attachHeader(viewHolder, entry, prev);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return new ViewHolder(inflater.inflate(LAYOUT, parent, false));
    }

    private ViewHolder attachHeader(ViewHolder holder, TaskEntry entry, TaskEntry prev) {
        String entryTitle = getTitleFromEntry(entry);

        if (entryTitle.equals(OVERDUE))
            holder.layout.setAlpha(0.4f);
        else holder.layout.setAlpha(1f);

        if (mDefaultFilter.isOrdered()) {
            holder.headerText.setText(entryTitle);
            holder.header.setVisibility(View.VISIBLE);
            return holder;
        }

        if (prev != null) {
            String prevTitle = getTitleFromEntry(prev);

            if (!entryTitle.equals(prevTitle)) {
                holder.headerText.setText(entryTitle);
                holder.header.setVisibility(View.VISIBLE);
            } else {
                holder.header.setVisibility(View.GONE);
            }
        } else {
            holder.headerText.setText(entryTitle);
            holder.header.setVisibility(View.VISIBLE);
        }

        return holder;
    }

    private String getTitleFromEntry(TaskEntry entry) {
        if ((System.currentTimeMillis() > entry.getTtlTimestamp()) &&
                (mDefaultFilter.getType() == DbTasksFilter.ACTIVE_TASK)) {
            return OVERDUE;
        } else {
            Calendar entryDate = Calendar.getInstance();
            entryDate.setTimeInMillis(entry.getTtlTimestamp());
            int diffDay = Math.abs(
                    entryDate.get(Calendar.DAY_OF_YEAR) -
                            Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

            return getHeaderTitleByNum(diffDay, entryDate);
        }
    }

    private String getHeaderTitleByNum(int num, Calendar entryDate) {
        if (num < 3) {
            switch (mDefaultFilter.getType()) {
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
            updateCursor(mDatabase.getCursor(mDefaultFilter));
        } else {
            updateCursor(cursor[0]);
        }
    }

    public void updateFilter(DbFilter filter) {
        mDefaultFilter = filter;
    }

    private void updateCursor(Cursor cursor) {
        changeCursor(cursor);
    }

    public int getDataType() {
        return mDefaultFilter.getType();
    }

    private void updateOrders() {
        mDatabase.startTransaction(aVoid -> {
            for (int position = 0; position < getItemCount(); position++) {
                TaskEntry current = mDatabase.getEntryById((int) getItemId(getMovedPosition(position)));
                current.setOrder(position);
                mDatabase.updateEntry(current);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener, ItemTouchHelperViewHolder
    {
        View header;
        TextView headerText;
        TextView description;

        public View layout;
        public TextView title;
        public TextView ttl;

        public ViewHolder(View view) {
            super(view);
            this.layout = view.findViewById(R.id.task_layout);
            this.header = view.findViewById(R.id.task_header);
            this.headerText = (TextView) view.findViewById(R.id.header_text);
            this.title = (TextView) view.findViewById(R.id.task_item_title);
            this.description = (TextView) view.findViewById(R.id.task_item_description);
            this.ttl = (TextView) view.findViewById(R.id.task_item_deadline);

            layout.setOnClickListener(this);
            layout.setOnLongClickListener(this);

            layout.setOnTouchListener((v, event) -> {
                if (mDefaultFilter.isOrdered()) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mFragment.onStartDrag(ViewHolder.this);
                    }
                }

                return false;
            });
        }

        @Override
        public void onClick(View v) {
            mFragment.onItemClick(this, getAdapterPosition(), TasksListAdapter.this.getItemId(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            mFragment.onItemLongClick(this, getAdapterPosition());
            return true;
        }

        @Override
        public void onItemSelected() {}

        @Override
        public void onItemClear() {
            updateOrders();
        }
    }
}
