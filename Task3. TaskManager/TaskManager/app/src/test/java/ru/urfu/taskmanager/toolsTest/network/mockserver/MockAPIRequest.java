package ru.urfu.taskmanager.toolsTest.network.mockserver;

import android.accounts.NetworkErrorException;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import ru.urfu.taskmanager.network.APICallbackInterface;
import ru.urfu.taskmanager.network.APIRequestInterface;
import ru.urfu.taskmanager.network.APIResponse;
import ru.urfu.taskmanager.tools.JSONFactory;

public class MockAPIRequest<S, R> implements APIRequestInterface<R>
{
    private MockServer<S> mockServer;
    private APIRequestInterface<R> mockableRequest;

    public MockAPIRequest(MockServer<S> server, APIRequestInterface<R> request) {
        this.mockServer = server;
        this.mockableRequest = request;
    }

    @Override
    public void send(APICallbackInterface<R> cb)
    {
        String response = mockServer.handleRequest(mockableRequest);

        APIResponse<R> apiResponse;

        if (getParseType() != null) {
            apiResponse = JSONFactory.fromJson(response,
                    TypeToken.getParameterized(APIResponse.class, getParseType())
                            .getType());
        } else {
            apiResponse = JSONFactory.fromJson(response,
                    new TypeToken<APIResponse<R>>(){}.getType());
        }

        if (apiResponse.getStatus().equals(APIResponse.STATUS_OK)) {
            cb.onResponse(apiResponse);
        } else {
            cb.onFailure(new NetworkErrorException(
                    "The server response is different than expected."));
        }
    }

    @Override
    public HttpUriRequest getRequest() {
        return mockableRequest.getRequest();
    }

    @Override
    public Type getParseType() {
        return mockableRequest.getParseType();
    }
}
