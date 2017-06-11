package ru.urfu.taskmanager.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("unused")
public class TaskEntryCouples
        implements Parcelable, Iterable<TaskEntryCouples.Couple>
{
    private ArrayList<Couple> data;

    public TaskEntryCouples() {
        this.data = new ArrayList<>();
    }

    private TaskEntryCouples(Parcel in) {
        data = in.createTypedArrayList(Couple.CREATOR);
    }

    public static final Creator<TaskEntryCouples> CREATOR = new Creator<TaskEntryCouples>()
    {
        @Override
        public TaskEntryCouples createFromParcel(Parcel in) {
            return new TaskEntryCouples(in);
        }

        @Override
        public TaskEntryCouples[] newArray(int size) {
            return new TaskEntryCouples[size];
        }
    };

    public void put(TaskEntry key, TaskEntry value) {
        data.add(new Couple(key, value));
    }

    public Couple remove(int index) {
        return data.remove(index);
    }

    public boolean remove(Couple couple) {
        return data.remove(couple);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
    }

    @Override
    public Iterator<Couple> iterator() {
        return data.iterator();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(data);
    }


    public static class Couple implements Parcelable
    {
        private TaskEntry key;
        private TaskEntry value;

        private Couple(TaskEntry key, TaskEntry value) {
            this.key = key;
            this.value = value;
        }

        Couple(Parcel in) {
            key = in.readParcelable(TaskEntry.class.getClassLoader());
            value = in.readParcelable(TaskEntry.class.getClassLoader());
        }

        public static final Creator<Couple> CREATOR = new Creator<Couple>()
        {
            @Override
            public Couple createFromParcel(Parcel in) {
                return new Couple(in);
            }

            @Override
            public Couple[] newArray(int size) {
                return new Couple[size];
            }
        };

        public TaskEntry getKey() {
            return key;
        }

        public TaskEntry getValue() {
            return value;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(key, flags);
            dest.writeParcelable(value, flags);
        }
    }
}