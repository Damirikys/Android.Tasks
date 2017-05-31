package ru.urfu.taskmanager.data.network;

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
