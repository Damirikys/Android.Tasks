package ru.urfu.taskmanager.mockserver;

import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import ru.urfu.taskmanager.network.APIRequestInterface;
import ru.urfu.taskmanager.network.APIResponse;
import ru.urfu.taskmanager.tools.JSONFactory;

public class MockServer<T>
{
    private static final String TAG = "MOCK_SERVER";
    private static final String UNDEFINED =
            "<h1>404 Not Found</h1>\n" + "<h3>The page you have requested could not be found.</h3>";
    private static final String NOT_FOUND =
            "{\n" + "  \"status\": \"error\",\n" + "  \"error\": \"not_found\"\n" + "}";

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";

    private Map<Integer, List<T>> mockDatabase = new LinkedTreeMap<>();

    public <R> String handleRequest(APIRequestInterface<R> apiRequestInterface)
    {
        try {
            Log.d(TAG, "handle request: " + apiRequestInterface.getRequest().toString());
            return handleResponse(apiRequestInterface);
        } catch (Exception e) {
            return "";
        }
    }

    private <R> String handleResponse(APIRequestInterface<R> apiRequestInterface) throws IOException {
        HttpUriRequest request = apiRequestInterface.getRequest();
        final String method = request.getMethod();
        final List<String> options = new ArrayList<>(Arrays.asList(request.getURI().getPath().split("/")));
        options.removeAll(Collections.singleton(""));
        return handleOptions(method, options, request);
    }

    private String handleOptions(final String method,
                                 final List<String> options,
                                 HttpUriRequest request) throws IOException {
        if (options.get(0).equals("user")) {
            final int userId = Integer.parseInt(options.get(1));

            if (options.get(2).equals("note")) {
                final int noteId = Integer.parseInt(options.get(3));

                switch (method) {
                    case METHOD_GET:
                    {
                        try {
                            List<T> userData = mockDatabase.get(userId);
                            T entry = userData.get(noteId);

                            APIResponse<T> apiResponse = new APIResponse<>(APIResponse.STATUS_OK, null, entry);
                            return JSONFactory.toJson(apiResponse, TypeToken.getParameterized(APIResponse.class,
                                    new TypeToken<T>(){}.getType()).getType());
                        } catch (Exception e) {
                            return NOT_FOUND;
                        }

                    }
                    case METHOD_POST:
                    {
                        String stringRequest = convertStreamToString(((HttpPost) request).getEntity().getContent());

                        T entry = JSONFactory.fromJson(stringRequest, new TypeToken<T>() {}.getType());

                        APIResponse<Void> apiResponse;

                        List<T> data = mockDatabase.get(userId);
                        if (data != null) {
                            data.set(noteId, entry);
                            apiResponse = new APIResponse<>(APIResponse.STATUS_OK, null, null);
                        } else {
                            apiResponse = new APIResponse<>(APIResponse.NOT_FOUND, null, null);
                        }

                        return JSONFactory.toJson(apiResponse, APIResponse.class);
                    }
                    case METHOD_DELETE:
                    {
                        APIResponse<Void> apiResponse;

                        if (mockDatabase.get(userId) != null) {
                            mockDatabase.get(userId).remove(noteId);
                            apiResponse = new APIResponse<>(APIResponse.STATUS_OK, null, null);
                        } else {
                            apiResponse = new APIResponse<>(APIResponse.NOT_FOUND, null, null);
                        }

                        return JSONFactory.toJson(apiResponse, APIResponse.class);
                    }
                    default: return UNDEFINED;
                }
            } else if (options.get(2).equals("notes")) {
                switch (method) {
                    case METHOD_POST:
                    {
                        String stringRequest = convertStreamToString(((HttpPost) request).getEntity().getContent());
                        T entry = JSONFactory.fromJson(stringRequest, new TypeToken<T>() {}.getType());

                        APIResponse<Integer> apiResponse = new APIResponse<>(APIResponse.STATUS_OK, null,
                                addEntry(userId, entry));

                        return JSONFactory.toJson(apiResponse, new TypeToken<APIResponse<Integer>>(){}.getType());
                    }
                    case METHOD_GET:
                    {
                        List<T> data = mockDatabase.get(userId);

                        APIResponse<List<T>> apiResponse = new APIResponse<>(APIResponse.STATUS_OK, null,
                                (data != null) ? data : new ArrayList<T>());

                        return JSONFactory.toJson(apiResponse,
                                TypeToken.getParameterized(APIResponse.class, List.class,
                                        new TypeToken<T>() {}.getType()).getType());
                    }
                    default: return UNDEFINED;
                }
            } else {
                return UNDEFINED;
            }
        }

        return UNDEFINED;
    }

    private int addEntry(int userId, T entry) {
        List<T> userData = mockDatabase.get(userId);
        if (userData != null) {
            userData.add(entry);
        } else  {
            userData = new ArrayList<>();
            userData.add(entry);
            mockDatabase.put(userId, userData);
        }

        return userData.size() - 1;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
