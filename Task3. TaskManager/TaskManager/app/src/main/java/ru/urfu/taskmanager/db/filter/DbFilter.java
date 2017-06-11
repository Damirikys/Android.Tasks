package ru.urfu.taskmanager.db.filter;

public interface DbFilter
{
    String[] getColumns();

    String getWhereClause();

    String[] getSelectionArgs();

    String getGroupBy();

    String getHaving();

    String getOrderBy();

    boolean isOrdered();

    int getType();
}
