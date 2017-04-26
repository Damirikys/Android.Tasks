package ru.urfu.taskmanager.utils.db;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksFilter
{
    public static final int ACTIVE_TASK = 0;
    public static final int COMPLETED_TASK = 1;

    private int type = ACTIVE_TASK;
    private Map<String, String> whereClause = new HashMap<>();
    private String groupBy;

    private TasksFilter(){}

    public String[] getColumns() {
        return null;
    }

    public String getWhereClause() {
        List<String> keys = new ArrayList<>(whereClause.keySet());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            builder.append(keys.get(i));
            builder.append("=");
            builder.append(whereClause.get(keys.get(i)));
            if (i != keys.size() - 1) builder.append(" AND ");
        }

        return builder.toString();
    }

    public String[] getSelectionArgs() {
        return null;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public String getHaving() {
        return null;
    }

    public String getOrderBy() {
        return null;
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
            filter.whereClause.put(TasksDatabaseHelper.COMPLETED, String.valueOf(type));
            return this;
        }

        public Builder setGroupBy(String column) {
            filter.groupBy = column;
            return this;
        }

        public TasksFilter build() {
            return filter;
        }
    }
}