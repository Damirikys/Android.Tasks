package ru.urfu.taskmanager.network;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import ru.urfu.taskmanager.entities.User;
import ru.urfu.taskmanager.tools.JSONFactory;

public class APIService<T> implements NotesBackendService<T>
{
    private static final String SERVER_BASE_URL = "https://notesbackend-yufimtsev.rhcloud.com";
    private static final String BODY_ENCODE = "utf-8";

    private Type typeToken;
    private String serviceUrl;

    public APIService(User user) {
        setServerBaseUrl(user, SERVER_BASE_URL);
        typeToken = new TypeToken<T>(){}.getType();
    }

    public final void setServerBaseUrl(User user, String serverBaseUrl) {
        serviceUrl = serverBaseUrl + "/user/" + user.getUserId() + "/";
    }

    public APIService<T> setRawType(Class clazz) {
        typeToken = clazz;
        return this;
    }

    public APIRequestInterface<Collection<T>> getUserNotes() {
        HttpGet request = new HttpGet(serviceUrl + "notes");
        request.setHeader("Content-Type", "application/json");

        return new APIRequest<>(request, TypeToken.getParameterized(Collection.class, typeToken).getType());
    }

    public APIRequest<T> getNoteById(int noteId) {
        HttpGet request = new HttpGet(serviceUrl + "note/" + noteId);
        request.setHeader("Content-Type", "application/json");

        return new APIRequest<>(request, typeToken);
    }

    public APIRequest<Integer> createNote(T entry) {
        HttpPost request = new HttpPost(serviceUrl + "notes");
        request.setHeader("Content-Type", "application/json");

        String json = JSONFactory.toJson(entry, typeToken);
        request.setEntity(new StringEntity(json, BODY_ENCODE));

        return new APIRequest<>(request, Integer.class);
    }

    public APIRequest<Void> editNote(T entry, int id) {
        HttpPost request = new HttpPost(serviceUrl + "note/" + id);
        request.setHeader("Content-Type", "application/json");

        String json = JSONFactory.toJson(entry, typeToken);
        request.setEntity(new StringEntity(json, BODY_ENCODE));

        return new APIRequest<>(request);
    }

    public APIRequest<Void> deleteNote(int id) {
        HttpDelete request = new HttpDelete(serviceUrl + "note/" + id);
        return new APIRequest<>(request);
    }
}
