package ru.urfu.taskmanager.utils.db;

import java.util.ArrayList;
import java.util.List;

import static android.text.format.DateUtils.DAY_IN_MILLIS;

public class TasksFilter
{
    private static final int ALL_TASK = -1;
    public static final int ACTIVE_TASK = 0;
    public static final int COMPLETED_TASK = 1;
    public static final String FRONT = "ASC";
    public static final String REVERSE = "DESC";
    public static final TasksFilter.Builder DEFAULT_BUILDER = new Builder(new TasksFilter(), true);

    private int mType;
    private List<String> mWhereClause;
    private List<String> mGroupBy;
    private String mOrderBy;
    private String mOrientation;

    private TasksFilter() {
        this.mType = ALL_TASK;
        this.mWhereClause = new ArrayList<>();
        this.mGroupBy = new ArrayList<>();
        this.mOrderBy = TasksDatabaseHelper.TTL;
        this.mOrientation = FRONT;
    }

    private TasksFilter(TasksFilter other) {
        this.mType = other.mType;
        this.mWhereClause = new ArrayList<>(other.mWhereClause);
        this.mGroupBy = new ArrayList<>(other.mGroupBy);
        this.mOrderBy = other.mOrderBy;
        this.mOrientation = other.mOrientation;
    }

    public static Builder builder() {
        return new Builder(new TasksFilter());
    }

    String[] getColumns() {
        return null;
    }

    String getWhereClause() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mWhereClause.size(); i++) {
            builder.append(mWhereClause.get(i));
            if (i != mWhereClause.size() - 1) builder.append(" AND ");
        }

        return (mWhereClause.isEmpty()) ? null : builder.toString();
    }

    String[] getSelectionArgs() {
        return null;
    }

    String getGroupBy() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mGroupBy.size(); i++) {
            builder.append(mGroupBy.get(i));
            if (i != mGroupBy.size() - 1) builder.append(", ");
        }

        return (mGroupBy.isEmpty()) ? null : builder.toString();
    }

    String getHaving() {
        return null;
    }

    String getOrderBy() {
        return mOrderBy + " " + mOrientation;
    }

    public int getType() {
        return mType;
    }

    /* TaskFilter.Builder class */
    public static class Builder
    {
        private TasksFilter mFilter;
        private boolean mDefault;

        private Builder(TasksFilter filter, boolean isDefault) {
            this.mFilter = filter;
            this.mDefault = isDefault;
        }

        private Builder(TasksFilter filter) {
            this.mFilter = filter;
            this.mDefault = false;
        }

        public boolean isDefault() {
            return mDefault;
        }

        public Builder setType(int type) {
            mFilter.mType = type;
            if (type != ALL_TASK) {
                mFilter.mWhereClause.add(TasksDatabaseHelper.COMPLETED + "=" + String.valueOf(type));
            }
            return this;
        }

        public Builder setOrientation(String orientation) {
            mFilter.mOrientation = orientation;
            return this;
        }

        public Builder sortBy(String column) {
            mFilter.mOrderBy = column;
            return this;
        }

        public Builder fromDate(long unix) {
            return fromDateRange(unix, unix);
        }

        public Builder fromDateRange(long start, long end) {
            mFilter.mWhereClause.add(TasksDatabaseHelper.TTL + ">" + String.valueOf(start));
            mFilter.mWhereClause.add(TasksDatabaseHelper.TTL + "<" + String.valueOf(end + DAY_IN_MILLIS));

            return this;
        }

        public Builder fromColor(int color) {
            mFilter.mWhereClause.add(TasksDatabaseHelper.DECORATE_COLOR + "=" + String.valueOf(color));

            return this;
        }

        public Builder groupBy(String type) {
            mFilter.mGroupBy.add(type);
            return this;
        }

        public Builder startsWith(String column, String query) {
            mFilter.mWhereClause.add(column + " LIKE '" + query + "%'");

            return this;
        }

        public TasksFilter build() {
            return mFilter;
        }

        public Builder copy() {
            return (mDefault) ? new Builder(new TasksFilter(), true) : new Builder(new TasksFilter(mFilter));
        }
    }
}