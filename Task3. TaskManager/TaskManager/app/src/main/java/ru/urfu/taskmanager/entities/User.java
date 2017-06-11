package ru.urfu.taskmanager.entities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import ru.urfu.taskmanager.network.APIServiceExecutor;
import ru.urfu.taskmanager.network.APIService;

public final class User
{
    private static User activeUser;

    private int mId;
    private String mLogin;
    private APIService<TaskEntry> mApiService;
    private APIServiceExecutor mAPIServiceExecutor;
    private String deviceIdentifier;

    private User(Context context, String mLogin) {
        this.mId = Math.abs(mLogin.hashCode());
        this.mLogin = mLogin;
        this.deviceIdentifier = initDeviceId(context);
        this.mAPIServiceExecutor = new APIServiceExecutor(context,
                mApiService = new APIService<TaskEntry>(this).setRawType(TaskEntry.class));
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

    public APIService<TaskEntry> getService() {
        return mApiService;
    }

    @SuppressLint("HardwareIds")
    private static String initDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void doLogin(Context context, String login) {
        activeUser = new User(context, login);
    }

    public static User getActiveUser() {
        return activeUser;
    }
}
