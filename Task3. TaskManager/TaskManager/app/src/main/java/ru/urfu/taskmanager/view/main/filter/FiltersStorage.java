package ru.urfu.taskmanager.view.main.filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import ru.urfu.taskmanager.db.filter.DbTasksFilter;
import ru.urfu.taskmanager.tools.JSONFactory;

public final class FiltersStorage
{
    private static final String REPOSITORY_NAME = "ru.urfu.taskmanager.filters_storages";
    private static final FiltersStorage REPOSITORY = new FiltersStorage();
    private SharedPreferences mStorage;

    public void putBuilder(String name, DbTasksFilter.Builder builder) {
        SharedPreferences.Editor editor = mStorage.edit();
        editor.putString(name, JSONFactory.toJson(builder, DbTasksFilter.Builder.class));
        editor.apply();
    }

    public DbTasksFilter.Builder getBuilder(@NonNull String key) {
        return JSONFactory.fromJson(mStorage.getString(key, ""), DbTasksFilter.Builder.class);
    }

    public Map<String, DbTasksFilter.Builder> getBuilders() {
        Map<String, DbTasksFilter.Builder> items = new HashMap<>();

        for (String key : mStorage.getAll().keySet()) {
            items.put(key, JSONFactory.fromJson(mStorage.getString(key, ""),
                    DbTasksFilter.Builder.class));
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
