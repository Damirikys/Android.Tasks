package ru.urfu.taskmanager.view.main.fragments.adapters;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.view.main.fragments.helper.CursorProvider;


abstract class AbstractTaskListAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
    implements CursorProvider
{
    static final String OVERDUE = "Просрочено";

    static final int LAYOUT = R.layout.task_list_item;

    static final String[] ACTIVE_DAYS = new String[]{
            "Сегодня", "Завтра", "Послезавтра"
    };

    static final String[] COMPLETED_DAYS = new String[]{
            "Сегодня", "Вчера", "Позавчера"
    };

    private List<Integer> mMovedPositions;

    private Cursor mCursor;

    private boolean mDataValid;

    private int mRowIdColumn;

    private DataSetObserver mDataSetObserver;

    AbstractTaskListAdapter(Cursor cursor) {
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    @Override
    public void onItemDismiss(int position) {}

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        swapPositions(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    protected abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, mCursor);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    private Cursor swapCursor(Cursor newCursor) {
        if (newCursor == null) return mCursor;

        if (newCursor == mCursor) {
            return null;
        }

        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mDataSetObserver != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
        mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
        mDataValid = true;
        notifyDataSetChanged();

        mMovedPositions = new LinkedList<>();
        for (int i = 0; i < newCursor.getCount(); i++) {
            mMovedPositions.add(i);
        }

        return oldCursor;
    }

    private void swapPositions(int from, int to) {
        Collections.swap(mMovedPositions, from, to);
    }

    int getMovedPosition(int oldPosition) {
        return mMovedPositions.get(oldPosition);
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}
