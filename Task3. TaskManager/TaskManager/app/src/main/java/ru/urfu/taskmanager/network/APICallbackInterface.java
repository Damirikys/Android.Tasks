package ru.urfu.taskmanager.network;

public interface APICallbackInterface<T>
{
    void onResponse(APIResponse<T> response);

    void onFailure(Throwable t);
}
