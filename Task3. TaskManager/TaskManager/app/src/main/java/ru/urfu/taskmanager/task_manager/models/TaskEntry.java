package ru.urfu.taskmanager.task_manager.models;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;

import ru.urfu.taskmanager.auth.models.User;
import ru.urfu.taskmanager.utils.tools.ISO8601;
import ru.urfu.taskmanager.utils.tools.JSONFactory;

public class TaskEntry implements Parcelable
{
    private transient int mAuthorId = User.getActiveUser().getUserId();

    private transient int mId;

    private transient Integer mOrder = null;

    @SerializedName("id")
    private Integer mEntryId;

    @SerializedName("extra")
    private String deviceId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("created")
    private String mCreated;

    @SerializedName("edited")
    private String mEdited;

    @SerializedName("viewed")
    private String mTimeToLive;

    @SerializedName("imageUrl")
    private String mImageUrl;

    @SerializedName("color")
    private String mColor;

    public TaskEntry() {
    }

    public TaskEntry(int id) {
        this.mId = id;
        this.mEntryId = null;
        this.deviceId = User.getActiveUser().getDeviceIdentifier();
    }

    protected TaskEntry(Parcel in) {
        mId = in.readInt();
        deviceId = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mCreated = in.readString();
        mEdited = in.readString();
        mTimeToLive = in.readString();
        mImageUrl = in.readString();
        mColor = in.readString();
    }

    public static final Creator<TaskEntry> CREATOR = new Creator<TaskEntry>()
    {
        @Override
        public TaskEntry createFromParcel(Parcel in) {
            return new TaskEntry(in);
        }

        @Override
        public TaskEntry[] newArray(int size) {
            return new TaskEntry[size];
        }
    };

    public TaskEntry setDeviceIdentifier(String hash) {
        this.deviceId = hash;
        return this;
    }

    public String getDeviceIdentifier() {
        return deviceId;
    }

    public int getAuthorId() {
        return mAuthorId;
    }

    public TaskEntry setId(int id) {
        this.mId = id;
        return this;
    }

    public int getId() {
        return mId;
    }

    public TaskEntry setEntryId(int id) {
        this.mEntryId = id;
        return this;
    }

    public Integer getEntryId() {
        return mEntryId;
    }

    public String getTitle() {
        return mTitle;
    }

    public TaskEntry setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public String getDescription() {
        return mDescription;
    }

    public TaskEntry setDescription(String description) {
        this.mDescription = description;
        return this;
    }

    public TaskEntry setImageUrl(String url) {
        this.mImageUrl = url;
        return this;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getTtl() {
        return mTimeToLive;
    }

    public long getTtlTimestamp() {
        try {
            return ISO8601.toTimestamp(mTimeToLive);
        } catch (ParseException e) {
            e.printStackTrace();
            return Long.MIN_VALUE;
        }
    }

    public TaskEntry setTtl(long ttl) {
        this.mTimeToLive = ISO8601.fromTimestamp(ttl);
        return this;
    }

    public TaskEntry setCreated(long created) {
        this.mCreated = ISO8601.fromTimestamp(created);
        return this;
    }

    public String getCreated() {
        return mCreated;
    }

    public long getCreatedTimestamp() {
        try {
            return ISO8601.toTimestamp(mCreated);
        } catch (Exception e) {
            e.printStackTrace();
            return Long.MIN_VALUE;
        }
    }

    public TaskEntry setEdited(long edited) {
        this.mEdited = ISO8601.fromTimestamp(edited);
        return this;
    }

    public String getEdited() {
        return mEdited;
    }

    public long getEditedTimestamp() {
        try {
            return ISO8601.toTimestamp(mEdited);
        } catch (ParseException e) {
            e.printStackTrace();
            return Long.MIN_VALUE;
        }
    }

    public String getColor() {
        return mColor;
    }

    public int getColorInt() {
        return Color.parseColor(mColor);
    }

    public TaskEntry setColor(@ColorInt int color) {
        this.mColor = String.format("#%06X", (0xFFFFFF & color));
        return this;
    }

    public boolean isCompleted() {
        return mTimeToLive.equals(mEdited);
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 89 * hash + (mTitle != null ? mTitle.hashCode() : 0);
        hash = 89 * hash + (mDescription != null ? mDescription.hashCode() : 0);
        hash = 89 * hash + (mCreated != null ? (int) getCreatedTimestamp() : 0);
        hash = 89 * hash + (mEdited != null ? (int) getEditedTimestamp() : 0);
        hash = 89 * hash + (mTimeToLive != null ? (int) getTtlTimestamp() : 0);
        hash = 89 * hash + (mImageUrl != null ? mImageUrl.hashCode() : 0);
        hash = 89 * hash + (mColor != null ? mColor.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            TaskEntry other = (TaskEntry) obj;
            return this.mEntryId.equals(other.mEntryId);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return JSONFactory.toJson(this, TaskEntry.class);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(deviceId);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mCreated);
        dest.writeString(mEdited);
        dest.writeString(mTimeToLive);
        dest.writeString(mImageUrl);
        dest.writeString(mColor);
    }

    public Integer getOrder() {
        return mOrder;
    }

    public TaskEntry setOrder(int mOrder) {
        this.mOrder = mOrder;
        return this;
    }
}
