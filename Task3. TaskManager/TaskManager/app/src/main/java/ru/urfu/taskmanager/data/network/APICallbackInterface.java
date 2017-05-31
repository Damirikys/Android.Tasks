package ru.urfu.taskmanager.data.network;

interface APICallbackInterface<T>
{
    void onResponse(APIResponse<T> response);

    void onFailure(Throwable t);
}
