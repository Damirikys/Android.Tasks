package ru.urfu.taskmanager.data.network;

import android.accounts.NetworkErrorException;
import android.net.ParseException;

import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import ru.urfu.taskmanager.utils.tools.JSONFactory;

public class APIRequest<T>
{
    private HttpUriRequest request;
    private Type parseType;

    APIRequest(HttpUriRequest request) {
        this.request = request;
    }

    APIRequest(HttpUriRequest request, Type type) {
        this.request = request;
        this.parseType = type;
    }

    public void send(APICallbackInterface<T> cb)
    {
        new Thread(() ->
        {
            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;

            try
            {
                client = HttpClientBuilder.create().build();
                response = client.execute(request);

                APIResponse<T> apiResponse = null;

                if(response.getEntity() != null)
                {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getEntity().getContent()));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null)
                    {
                        stringBuilder.append(line);
                    }

                    if (parseType != null) {
                        apiResponse = JSONFactory.fromJson(stringBuilder.toString(),
                                TypeToken.getParameterized(APIResponse.class, parseType)
                                        .getType());
                    } else {
                        apiResponse = JSONFactory.fromJson(stringBuilder.toString(),
                                new TypeToken<APIResponse<T>>(){}.getType());
                    }

                    reader.close();
                }

                if (apiResponse != null) {
                    if (apiResponse.getStatus().equals(APIResponse.STATUS_OK)) {
                        cb.onResponse(apiResponse);
                    } else {
                        cb.onFailure(new NetworkErrorException(
                                "The server response is different than expected."));
                    }
                } else {
                    cb.onFailure(new NetworkErrorException(
                            "The server did not receive a reply."));
                }
            }
            catch (IOException | ParseException e)
            {
                cb.onFailure(e);
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    if (response != null) response.close();
                    if (client != null) client.close();
                }
                catch (IOException e)
                {
                    cb.onFailure(e);
                    e.printStackTrace();
                }
            }
        }).start();
    }
}