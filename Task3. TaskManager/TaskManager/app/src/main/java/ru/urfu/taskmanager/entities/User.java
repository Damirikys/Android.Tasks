package ru.urfu.taskmanager.entities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import ru.urfu.taskmanager.network.APIServiceExecutor;
import ru.urfu.taskmanager.network.APIService;
import ru.urfu.taskmanager.network.NotesBackendService;

public final class User
{
    private static User activeUser;

    private int mId;
    private String mLogin;
    private NotesBackendService<TaskEntry> mApiService;
    private APIServiceExecutor mAPIServiceExecutor;
    private String deviceIdentifier;

    private User(Context context, String mLogin) {
        this.mId = Math.abs(mLogin.hashCode());
        this.mLogin = mLogin;
        this.deviceIdentifier = initDeviceId(context);

        this.mApiService = new APIService<>(this);
        this.mApiService.setRawType(TaskEntry.class);

        this.mAPIServiceExecutor = new APIServiceExecutor(context, mApiService);
    }

    public void changeApiService(Context context, NotesBackendService<TaskEntry> apiService) {
        this.mApiService = apiService;
        this.mAPIServiceExecutor = new APIServiceExecutor(context, mApiService);
    }

    public int getUserId() {
        return mId;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public String getLogin() {
        return mLogin;
    }

    public APIServiceExecutor getExecutor() {
        return mAPIServiceExecutor;
    }

    public NotesBackendService<TaskEntry> getService() {
        return mApiService;
    }

    @SuppressLint("HardwareIds")
    private static String initDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static User doLogin(Context context, String login) {
        activeUser = new User(context, login);
        return activeUser;
    }

    public static User getActiveUser() {
        return activeUser;
    }
}
