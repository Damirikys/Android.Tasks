package ru.urfu.taskmanager.task_manager.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskEntry implements Parcelable
{
    private int id;
    private String title;
    private String description;
    private long ttl;
    private int color;
    private int isCompleted;

    public TaskEntry() {}

    public TaskEntry(int id){
        this.id = id;
    }

    private TaskEntry(Parcel in) {
        title = in.readString();
        description = in.readString();
        ttl = in.readLong();
        color = in.readInt();
        isCompleted = in.readInt();
    }

    public static final Creator<TaskEntry> CREATOR = new Creator<TaskEntry>() {
        @Override
        public TaskEntry createFromParcel(Parcel in) {
            return new TaskEntry(in);
        }

        @Override
        public TaskEntry[] newArray(int size) {
            return new TaskEntry[size];
        }
    };

    public TaskEntry setId(int id) {
        this.id = id;
        return this;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskEntry setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TaskEntry setDescription(String description) {
        this.description = description;
        return this;
    }

    public long getTtl() {
        return ttl;
    }

    public TaskEntry setTtl(long ttl) {
        this.ttl = ttl;
        return this;
    }

    public int getColor() {
        return color;
    }

    public TaskEntry setColor(int color) {
        this.color = color;
        return this;
    }

    public boolean isCompleted() {
        return (isCompleted == 1);
    }

    public TaskEntry setCompleted(boolean bool) {
        isCompleted = (bool) ? 1: 0;
        return this;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;

        hash = 89 * hash + (title != null ? title.hashCode() : 0);
        hash = 89 * hash + (description != null ? description.hashCode() : 0);
        hash = 89 * hash + (int) (ttl ^ (ttl >>> 32));
        hash = 89 * hash + color;

        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        try
        {
            TaskEntry other = (TaskEntry) obj;
            return this.id == other.id;
        }
        catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int describeContents() {
        return CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(ttl);
        dest.writeInt(color);
        dest.writeInt(isCompleted);
    }

    @Override
    public String toString() {
        return "[" + "title: " + title + "; " + "description: " + description + "; " +
                "ttl: " + ttl + "; " + "color: " + color + "]";
    }
}
