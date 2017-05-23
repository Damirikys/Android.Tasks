package ru.urfu.taskmanager.data.network;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import ru.urfu.taskmanager.auth.models.User;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.tools.JSONFactory;

public class APIService
{
    private static final String SERVER_BASE_URL = "https://notesbackend-yufimtsev.rhcloud.com/";
    private static final String BODY_ENCODE = "utf-8";

    private final String serviceUrl;

    public APIService(User user) {
        this.serviceUrl = SERVER_BASE_URL + "user/" + user.getUserId() + "/";
    }

    public <U extends Collection<TaskEntry>> APIRequest<U> getUserNotes() {
        HttpGet request = new HttpGet(serviceUrl + "notes");
        request.setHeader("Accept", "application/json");

        return new APIRequest<>(request, TypeToken.getParameterized(Collection.class, TaskEntry.class).getType());
    }

    public APIRequest<TaskEntry> getNoteById(int noteId) {
        HttpGet request = new HttpGet(serviceUrl + "note/" + noteId);
        request.setHeader("Accept", "application/json");

        return new APIRequest<>(request);
    }

    public APIRequest<Integer> createNote(TaskEntry entry) {
        HttpPost request = new HttpPost(serviceUrl + "notes");
        request.setHeader("Accept", "application/json");

        String json = JSONFactory.toJson(entry, TaskEntry.class);
        request.setEntity(new StringEntity(json, BODY_ENCODE));

        return new APIRequest<>(request, Integer.class);
    }

    public APIRequest<Void> editNote(TaskEntry entry) {
        HttpPost request = new HttpPost(serviceUrl + "note/" + entry.getEntryId());
        request.setHeader("Accept", "application/json");

        String json = JSONFactory.toJson(entry, TaskEntry.class);
        request.setEntity(new StringEntity(json, BODY_ENCODE));

        return new APIRequest<>(request);
    }

    public APIRequest<Void> deleteNote(int id) {
        HttpDelete request = new HttpDelete(serviceUrl + "note/" + id);
        request.setHeader("Accept", "application/json");

        return new APIRequest<>(request);
    }
}
