package ru.urfu.taskmanager.task_manager.main.filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.urfu.taskmanager.utils.db.TasksFilter;
import ru.urfu.taskmanager.utils.tools.JSONFactory;

public final class FiltersStorage
{
    private static final String REPOSITORY_NAME = "ru.urfu.taskmanager.filters_storages";
    private static final FiltersStorage REPOSITORY = new FiltersStorage();
    private SharedPreferences mStorage;

    public void putBuilder(String name, TasksFilter.Builder builder) {
        SharedPreferences.Editor editor = mStorage.edit();
        editor.putString(name, JSONFactory.toJson(builder, TasksFilter.Builder.class));
        editor.apply();
    }

    public TasksFilter.Builder getBuilder(@NonNull String key) {
        try {
            return JSONFactory.fromJson(mStorage.getString(key, ""), TasksFilter.Builder.class);
        } catch (IOException e) {
            return null;
        }
    }

    public Map<String, TasksFilter.Builder> getBuilders() {
        Map<String, TasksFilter.Builder> items = new HashMap<>();

        for (String key : mStorage.getAll().keySet()) {
            try {
                items.put(key, JSONFactory.fromJson(mStorage.getString(key, ""), TasksFilter.Builder.class));
            } catch (IOException ignored) {
            }
        }

        return items;
    }

    public static void init(Context context) {
        REPOSITORY.mStorage = context.getSharedPreferences(REPOSITORY_NAME, Context.MODE_PRIVATE);
    }

    public static FiltersStorage getStorage() {
        return REPOSITORY;
    }
}
