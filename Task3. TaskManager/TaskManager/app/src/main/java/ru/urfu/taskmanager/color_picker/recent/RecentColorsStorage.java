package ru.urfu.taskmanager.color_picker.recent;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class RecentColorsStorage
{
    private static final String REPOSITORY_NAME = "ru.urfu.taskmanager.colors_repository";
    private static final RecentColorsStorage REPOSITORY = new RecentColorsStorage();
    private SharedPreferences mStorage;

    public static void init(Context context) {
        REPOSITORY.mStorage = context.getSharedPreferences(REPOSITORY_NAME, Context.MODE_PRIVATE);
    }

    public static RecentColorsStorage getRepository() {
        return REPOSITORY;
    }

    public void putItem(int color) {
        if (isExist(color)) return;
        SharedPreferences.Editor editor = mStorage.edit();
        editor.putInt(String.valueOf(color), color);
        editor.apply();
    }

    @SuppressWarnings("unchecked")
    List<Integer> getItems() {
        List<Integer> items = new ArrayList<>();
        Map<String, ?> map = mStorage.getAll();
        Collection<? extends Integer> entries = (Collection<? extends Integer>) map.values();
        for (int number : entries)
            items.add(number);

        return items;
    }

    private boolean isExist(int color) {
        return mStorage.contains(String.valueOf(color));
    }
}
