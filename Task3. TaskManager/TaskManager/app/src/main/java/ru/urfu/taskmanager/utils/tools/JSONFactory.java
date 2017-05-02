package ru.urfu.taskmanager.utils.tools;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.lang.reflect.Type;

public class JSONFactory
{
    private static final Moshi moshi = new Moshi.Builder().build();

    public static <T> String toJson(T object, Class<T> _class) {
        JsonAdapter<T> jsonAdapter = moshi.adapter(_class);
        return jsonAdapter.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> _class) throws IOException {
        JsonAdapter<T> jsonAdapter = moshi.adapter(_class);
        return jsonAdapter.fromJson(json);
    }

    public static <T> T fromJson(String json, Type type) throws IOException {
        JsonAdapter<T> jsonAdapter = moshi.adapter(type);
        return jsonAdapter.fromJson(json);
    }
}
