package ru.urfu.taskmanager.data.db;

public interface DbFilter
{
    String[] getColumns();

    String getWhereClause();

    String[] getSelectionArgs();

    String getGroupBy();

    String getHaving();

    String getOrderBy();

    int getType();
}
