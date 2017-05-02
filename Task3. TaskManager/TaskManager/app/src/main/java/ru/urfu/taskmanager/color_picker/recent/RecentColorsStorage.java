package ru.urfu.taskmanager.color_picker.recent;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RecentColorsStorage
{
    private static final String REPOSITORY_NAME = "ru.urfu.taskmanager.colors_repository";
    private static final RecentColorsStorage repository = new RecentColorsStorage();
    private SharedPreferences storage;

    public void putItem(int color) {
        if (isExist(color)) return;
        SharedPreferences.Editor editor = storage.edit();
        editor.putInt(String.valueOf(color), color);
        editor.apply();
    }

    public List<Integer> getItems() {
        List<Integer> items = new ArrayList<>();
        Map<String, ?> map = storage.getAll();
        for (Object o : map.values())
            items.add((int) o);

        return items;
    }

    public boolean isExist(int color) {
        return storage.contains(String.valueOf(color));
    }

    public static void init(Context context) {
        repository.storage = context.getSharedPreferences(REPOSITORY_NAME, Context.MODE_PRIVATE);
    }

    public static RecentColorsStorage getRepository() {
        return repository;
    }
}
