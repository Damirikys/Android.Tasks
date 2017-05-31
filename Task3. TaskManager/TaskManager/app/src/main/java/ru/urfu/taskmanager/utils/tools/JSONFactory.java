package ru.urfu.taskmanager.utils.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public final class JSONFactory
{
    private static final Gson GSON = new GsonBuilder().create();

    private JSONFactory() {}

    public static <T> String toJson(T object, Class<T> clazz) {
        return GSON.toJson(object, clazz);
    }


    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }
}
