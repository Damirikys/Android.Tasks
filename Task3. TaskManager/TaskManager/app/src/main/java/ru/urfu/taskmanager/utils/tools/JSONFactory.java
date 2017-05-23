package ru.urfu.taskmanager.utils.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Type;

public class JSONFactory
{
    private static final Gson gson = new GsonBuilder().create();

    public static <T> String toJson(T object, Class<T> _class) {
        return gson.toJson(object, _class);
    }


    public static <T> T fromJson(String json, Class<T> _class) throws IOException {
        return gson.fromJson(json, _class);
    }

    public static <T> T fromJson(String json, Type type) throws IOException {
        return gson.fromJson(json, type);
    }
}
