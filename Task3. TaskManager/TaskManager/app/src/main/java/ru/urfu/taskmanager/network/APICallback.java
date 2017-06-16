package ru.urfu.taskmanager.network;

public abstract class APICallback<T> implements APICallbackInterface<T>
{
    @Override
    public void onResponse(APIResponse<T> response) {
        // Stub!
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
    }
}
