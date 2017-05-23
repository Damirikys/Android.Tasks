package ru.urfu.taskmanager.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbTasksHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "DbTasks";
    public static final String TABLE_NAME = "tasks";
    public static final int DATABASE_VERSION = 1;

    public static final String ID = "_id";
    public static final String ORDER = "_order";
    public static final String USER_ID = "_user_id";
    public static final String ENTRY_ID = "_entry_id";
    public static final String TITLE = "_title";
    public static final String DESCRIPTION = "_description";
    public static final String TTL = "_timetolive";
    public static final String TIME_CREATED = "_time_created";
    public static final String TIME_EDITED = "_time_edited";
    public static final String DECORATE_COLOR = "_decorate_color";
    public static final String IMAGE_URL = "_img_url";

    public DbTasksHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + TABLE_NAME + "(" +
                ID + " integer primary key autoincrement," +
                ORDER + " integer," +
                USER_ID + " integer," +
                ENTRY_ID + " integer," +
                TITLE + " text," +
                DESCRIPTION + " text," +
                TTL + " text," +
                TIME_CREATED + " text," +
                TIME_EDITED + " text," +
                IMAGE_URL + " text," +
                DECORATE_COLOR + " integer" + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(database);
    }
}