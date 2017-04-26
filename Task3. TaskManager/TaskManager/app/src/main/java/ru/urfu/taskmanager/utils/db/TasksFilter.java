package ru.urfu.taskmanager.utils.db;

import java.util.ArrayList;
import java.util.List;

public class TasksFilter
{
    public static final int ACTIVE_TASK = 0;
    public static final int COMPLETED_TASK = 1;
    public static final String FRONT = "ASC";
    public static final String REVERSE = "DESC";

    private int type = ACTIVE_TASK;
    private List<String> whereClause = new ArrayList<>();
    private String groupBy;
    private String orderBy = FRONT;

    private TasksFilter(){}

    String[] getColumns() {
        return null;
    }

    String getWhereClause() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < whereClause.size(); i++) {
            builder.append(whereClause.get(i));
            if (i != whereClause.size() - 1) builder.append(" AND ");
        }

        return builder.toString();
    }

    String[] getSelectionArgs() {
        return null;
    }

    String getGroupBy() {
        return groupBy;
    }

    String getHaving() {
        return null;
    }

    String getOrderBy() {
        return (((groupBy == null) ? TasksDatabaseHelper.TTL : groupBy) + " " + orderBy);
    }

    public int getType() {
        return type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    {
        private TasksFilter filter;

        private Builder() {
            this.filter = new TasksFilter();
        }

        public Builder setType(int type) {
            filter.type = type;
            filter.whereClause.add(
                    TasksDatabaseHelper.COMPLETED + "=" + String.valueOf(type)
            );

            return this;
        }

        public Builder sortBy(String column) {
            filter.groupBy = column;
            return this;
        }

        public Builder fromDate(long unix) {
            return fromDateRange(unix, unix + 86400);
        }

        public Builder fromDateRange(long start, long end) {
            filter.whereClause.add(
                    TasksDatabaseHelper.TTL + ">" + String.valueOf(start));
            filter.whereClause.add(
                    TasksDatabaseHelper.TTL + "<" + String.valueOf(end)
            );

            return this;
        }

        public Builder fromColor(int color) {
            filter.whereClause.add(
                    TasksDatabaseHelper.DECORATE_COLOR + "=" + String.valueOf(color)
            );

            return this;
        }

        public Builder orderBy(String type) {
            filter.orderBy = type;
            return this;
        }

        public TasksFilter build() {
            return filter;
        }
    }
}