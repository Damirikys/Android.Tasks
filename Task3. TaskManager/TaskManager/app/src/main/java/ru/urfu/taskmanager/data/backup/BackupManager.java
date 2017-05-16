package ru.urfu.taskmanager.data.backup;

import android.os.Handler;
import android.os.HandlerThread;

import com.squareup.moshi.Types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.urfu.taskmanager.data.db.async.ExecuteController;
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

            String json = JSONFactory.toJson(provider.mData, provider.mDataClass);
            File itemList = new File(provider.mPath, provider.mFileName);
            try {
                FileOutputStream fos = new FileOutputStream(itemList);
                fos.write(json.getBytes());
                fos.close();

                provider.mExecuteController.onFinish(true);
            } catch (IOException e) {
                provider.mExecuteController.onFinish(false);
            }
        });
    }

    public <T> void importFrom(InputStream inputStream, Class<T> tClass, ExecuteController<List<T>> controller) throws FileNotFoundException {
        mWorkerHandler.post(() -> {
            controller.onStart();

            StringBuilder builder = new StringBuilder();
            try {
                if (inputStream == null)
                    throw new FileNotFoundException();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(inputStream)
                );

                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                    builder.append('\n');
                }

                br.close();

                List<T> entries = JSONFactory.fromJson(builder.toString(),
                        Types.newParameterizedType(List.class, tClass));

                controller.onFinish(entries);
            } catch (IOException e) {
                controller.onFinish(new ArrayList<>());
            }
        });
    }

    public static class DataProvider<T>
    {
        private String mPath;
        private String mFileName;

        private T mData;
        private Class<T> mDataClass;

        private ExecuteController<Boolean> mExecuteController;

        public DataProvider(String path,
                            String filename,
                            T object,
                            Class<T> objectClass,
                            ExecuteController<Boolean> controller)
        {
            this.mPath = path;
            this.mFileName = filename;
            this.mData = object;
            this.mDataClass = objectClass;
            this.mExecuteController = controller;
        }

    }
}
