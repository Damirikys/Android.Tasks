package ru.urfu.taskmanager.task_manager.main.tools;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ru.urfu.taskmanager.utils.db.async.ExecuteController;
import ru.urfu.taskmanager.utils.tools.JSONFactory;

public class BackupManager extends HandlerThread
{
    private static final String THREAD_NAME = "ru.urfu.taskmanager.BACKUP_MANAGER_THREAD";
    private Handler mWorkerHandler;

    public BackupManager() {
        super(THREAD_NAME);
        this.start();
    }

    @Override
    protected void onLooperPrepared() {
        mWorkerHandler = new Handler(getLooper());
    }

    public <T> void exportTo(DataProvider<T> provider) {
        mWorkerHandler.post(() -> {
            provider.mExecuteController.onStart();
            OutputStream outputStream = null;
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting()
                    .create();

            File file = new File(provider.mPath, provider.mFileName);

            try {
                outputStream = new FileOutputStream(file);
                BufferedWriter bufferedWriter;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,
                            StandardCharsets.UTF_8));
                } else {
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                }

                gson.toJson(provider.mData, provider.getType(), bufferedWriter);
                bufferedWriter.close();
                provider.mExecuteController.onFinish(true);
            } catch (IOException e) {
                e.printStackTrace();
                provider.mExecuteController.onFinish(false);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException ignored) {}
                }
            }
        });
    }

    public <T> void importFrom(InputStream inputStream, Class<T> _class, ExecuteController<List<T>> controller) throws FileNotFoundException {
        mWorkerHandler.post(() -> {
            controller.onStart();

            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting()
                    .create();
            try {
                InputStreamReader streamReader;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    streamReader = new InputStreamReader(inputStream,
                            StandardCharsets.UTF_8);
                } else {
                    streamReader = new InputStreamReader(inputStream, "UTF-8");
                }

                List<T> result = gson.fromJson(streamReader, TypeToken.getParameterized(List.class, _class).getType());
                streamReader.close();
                controller.onFinish(result);
            } catch (IOException e) {
                e.printStackTrace();
                controller.onFinish(new ArrayList<>());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ignored) {}
                }
            }
        });
    }

    public static class DataProvider<T>
    {
        private String mPath;
        private String mFileName;

        private T mData;

        private ExecuteController<Boolean> mExecuteController;

        public DataProvider(String path,
                            String filename,
                            T object,
                            ExecuteController<Boolean> controller)
        {
            this.mPath = path;
            this.mFileName = filename;
            this.mData = object;
            this.mExecuteController = controller;
        }

        private Type getType() {
            return new TypeToken<T>(){}.getType();
        }
    }
}
