package ru.urfu.taskmanager.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class APIResponse<T>
{
    public static final String STATUS_OK = "ok";
    public static final String PARSE_ERROR = "parse_error";
    public static final String QUERY_FAILED = "query_failed";
    public static final String NOT_FOUND = "not_found";

    private transient int statusCode;

    private String status;
    private String error;

    public APIResponse() {}

    public APIResponse(@NonNull String status, @Nullable String error, T data) {
        this.status = status;
        this.error = error;
        this.body = data;
    }

    @SerializedName("data")
    private T body;

    public T getBody() {
        return body;
    }

    public String getStatus() {
        return status;
    }

    public boolean isError() {
        return error != null;
    }
}
