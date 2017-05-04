package ru.urfu.taskmanager.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.urfu.taskmanager.task_manager.models.TaskEntry;

import static ru.urfu.taskmanager.utils.db.TasksFilter.COMPLETED_TASK;

public class TasksDatabase
{
    private static TasksDatabase sInstance;
    private SQLiteDatabase mDatabase;

    private TasksDatabase(Context c) {
        super();
        TasksDatabaseHelper dbHelper = new TasksDatabaseHelper(c);
        this.mDatabase = dbHelper.getWritableDatabase();
    }

    public static TasksDatabase getInstance() {
        return sInstance;
    }

    public static void init(Context applicationContext) {
        sInstance = new TasksDatabase(applicationContext);
    }

    public List<TaskEntry> getAllEntries() {
        List<TaskEntry> entries = new ArrayList<>();
        Cursor cursor = getCursor(TasksFilter.DEFAULT_BUILDER.build());
        cursor.moveToFirst();

        do {
            entries.add(getCurrentEntryFromCursor(cursor));
        } while (cursor.moveToNext());

        return entries;
    }

    public void insertEntry(TaskEntry entry) {
        mDatabase.insert(TasksDatabaseHelper.TABLE_NAME, null, contentValuesFrom(entry));
    }

    public void removeEntryById(int id) {
        mDatabase.delete(TasksDatabaseHelper.TABLE_NAME, TasksDatabaseHelper.ID + " = " + id, null);
    }

    public void updateEntry(TaskEntry entry) {
        mDatabase.update(TasksDatabaseHelper.TABLE_NAME, contentValuesFrom(entry), TasksDatabaseHelper.ID + " = " + entry.getId(), null);
    }

    public void replaceAll(List<TaskEntry> entries) {
        for (TaskEntry entry : entries) {
            insertEntry(entry);
        }
    }

    public TaskEntry getCurrentEntryFromCursor(Cursor cursor) {
        int id = cursor.getColumnIndex(TasksDatabaseHelper.ID);
        int title = cursor.getColumnIndex(TasksDatabaseHelper.TITLE);
        int timetolive = cursor.getColumnIndex(TasksDatabaseHelper.TTL);
        int time_edited = cursor.getColumnIndex(TasksDatabaseHelper.TIME_EDITED);
        int time_created = cursor.getColumnIndex(TasksDatabaseHelper.TIME_CREATED);
        int description = cursor.getColumnIndex(TasksDatabaseHelper.DESCRIPTION);
        int isCompleted = cursor.getColumnIndex(TasksDatabaseHelper.COMPLETED);
        int decorate_color = cursor.getColumnIndex(TasksDatabaseHelper.DECORATE_COLOR);

        return new TaskEntry(cursor.getInt(id))
                .setTitle(cursor.getString(title))
                .setDescription(cursor.getString(description))
                .setTtl(Long.valueOf(cursor.getString(timetolive)))
                .setCreated(Long.valueOf(cursor.getString(time_created)))
                .setEdited(Long.valueOf(cursor.getString(time_edited)))
                .setTtl(Long.valueOf(cursor.getString(timetolive)))
                .setColor(cursor.getInt(decorate_color))
                .setCompleted(cursor.getInt(isCompleted) == COMPLETED_TASK);
    }

    public TaskEntry getEntryById(int id) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + TasksDatabaseHelper.TABLE_NAME + " WHERE " + TasksDatabaseHelper.ID + "=" + id, null);

        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();
        return getCurrentEntryFromCursor(cursor);
    }

    public Cursor getCursor(TasksFilter filter) {
        return mDatabase.query(TasksDatabaseHelper.TABLE_NAME,
                filter.getColumns(),
                filter.getWhereClause(),
                filter.getSelectionArgs(),
                filter.getGroupBy(),
                filter.getHaving(),
                filter.getOrderBy()
        );
    }

    private ContentValues contentValuesFrom(TaskEntry entry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TasksDatabaseHelper.TTL, entry.getTtlTimestamp());
        contentValues.put(TasksDatabaseHelper.TITLE, entry.getTitle());
        contentValues.put(TasksDatabaseHelper.TIME_EDITED, entry.getEditedTimestamp());
        contentValues.put(TasksDatabaseHelper.TIME_CREATED, entry.getCreatedTimestamp());
        contentValues.put(TasksDatabaseHelper.COMPLETED, entry.isCompleted());
        contentValues.put(TasksDatabaseHelper.DESCRIPTION, entry.getDescription());
        contentValues.put(TasksDatabaseHelper.DECORATE_COLOR, entry.getColorInt());

        return contentValues;
    }
}
