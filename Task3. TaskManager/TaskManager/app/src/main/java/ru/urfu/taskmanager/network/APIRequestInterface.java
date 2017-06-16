package ru.urfu.taskmanager.network;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.client.methods.HttpUriRequest;

public interface APIRequestInterface<T>
{
    void send(APICallbackInterface<T> cb);

    HttpUriRequest getRequest();

    Type getParseType();
}
