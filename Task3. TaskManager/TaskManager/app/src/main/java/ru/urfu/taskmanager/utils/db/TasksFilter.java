package ru.urfu.taskmanager.utils.db;

import java.util.ArrayList;
import java.util.List;

public class TasksFilter
{
    private static final int ALL_TASK = -1;

    public static final int ACTIVE_TASK = 0;
    public static final int COMPLETED_TASK = 1;
    public static final String FRONT = "ASC";
    public static final String REVERSE = "DESC";
    public static final TasksFilter.Builder DEFAULT_BUILDER = new Builder(new TasksFilter(), true);

    private int type;
    private List<String> whereClause;
    private List<String> groupBy;
    private String orderBy;
    private String orientation;

    private TasksFilter() {
        this.type = ALL_TASK;
        this.whereClause = new ArrayList<>();
        this.groupBy = new ArrayList<>();
        this.orderBy = TasksDatabaseHelper.TTL;
        this.orientation = FRONT;
    }

    private TasksFilter(TasksFilter other) {
        this.type = other.type;
        this.whereClause = new ArrayList<>(other.whereClause);
        this.groupBy = new ArrayList<>(other.groupBy);
        this.orderBy = other.orderBy;
        this.orientation = other.orientation;
    }

    String[] getColumns() {
        return null;
    }

    String getWhereClause() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < whereClause.size(); i++) {
            builder.append(whereClause.get(i));
            if (i != whereClause.size() - 1) builder.append(" AND ");
        }

        return (whereClause.isEmpty()) ? null : builder.toString();
    }

    String[] getSelectionArgs() {
        return null;
    }

    String getGroupBy() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < groupBy.size(); i++) {
            builder.append(groupBy.get(i));
            if (i != groupBy.size() - 1) builder.append(", ");
        }

        return (groupBy.isEmpty()) ? null : builder.toString();
    }

    String getHaving() {
        return null;
    }

    String getOrderBy() {
        return orderBy + " " + orientation;
    }

    public int getType() {
        return type;
    }

    public static Builder builder() {
        return new Builder(new TasksFilter());
    }

    /* TaskFilter.Builder class */
    public static class Builder
    {
        private static final int DAY_IN_MILLIS = 86400 * 1000;
        private TasksFilter filter;
        private boolean isDefault;

        private Builder(TasksFilter filter, boolean isDefault) {
            this.filter = filter;
            this.isDefault = isDefault;
        }

        private Builder(TasksFilter filter) {
            this.filter = filter;
            this.isDefault = false;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public Builder setType(int type) {
            filter.type = type;
            if (type != ALL_TASK) {
                filter.whereClause.add(
                        TasksDatabaseHelper.COMPLETED + "=" + String.valueOf(type)
                );
            }
            return this;
        }

        public Builder setOrientation(String orientation) {
            filter.orientation = orientation;
            return this;
        }

        public Builder sortBy(String column) {
            filter.orderBy = column;
            return this;
        }

        public Builder fromDate(long unix) {
            return fromDateRange(unix, unix);
        }

        public Builder fromDateRange(long start, long end) {
            filter.whereClause.add(
                    TasksDatabaseHelper.TTL + ">" + String.valueOf(start));
            filter.whereClause.add(
                    TasksDatabaseHelper.TTL + "<" + String.valueOf(end + DAY_IN_MILLIS)
            );

            return this;
        }

        public Builder fromColor(int color) {
            filter.whereClause.add(
                    TasksDatabaseHelper.DECORATE_COLOR + "=" + String.valueOf(color)
            );

            return this;
        }

        public Builder groupBy(String type) {
            filter.groupBy.add(type);
            return this;
        }

        public Builder startsWith(String column, String query) {
            filter.whereClause.add(
                    column + " LIKE '" + query + "%'"
            );

            return this;
        }

        public TasksFilter build() {
            return filter;
        }

        public Builder copy() {
            return (isDefault) ? new Builder(new TasksFilter(), true) : new Builder(new TasksFilter(filter));
        }
    }
}