package ru.urfu.taskmanager.utils.db;

public interface DbFilter
{
    String[] getColumns();

    String getWhereClause();

    String[] getSelectionArgs();

    String getGroupBy();

    String getHaving();

    String getOrderBy();
}
