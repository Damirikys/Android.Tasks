package ru.urfu.taskmanager.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TasksDatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "TasksDatabase";
    public static final String TABLE_NAME = "tasks";
    public static final int DATABASE_VERSION = 1;

    public static final String ID = "_id";
    public static final String TITLE = "_title";
    public static final String DESCRIPTION = "_description";
    public static final String TTL = "_timetolive";
    public static final String TIME_CREATED = "_time_created";
    public static final String TIME_EDITED = "_time_edited";
    public static final String DECORATE_COLOR = "_decorate_color";
    public static final String COMPLETED = "_completed";

    public TasksDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL("create table " + TABLE_NAME + "("
                + ID + " integer primary key,"
                + TITLE + " text,"
                + DESCRIPTION + " text,"
                + TTL + " text,"
                + TIME_CREATED + " text,"
                + TIME_EDITED + " text,"
                + DECORATE_COLOR + " integer,"
                + COMPLETED + " integer"
                + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        database.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(database);
    }
}