package ru.urfu.applifecycle;

import android.content.res.Configuration;
import android.util.Log;
import android.widget.Toast;

public class Application extends android.app.Application
{
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, TAG + ": onCreate", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onLowMemory() {
        Toast.makeText(this, TAG + ": onLowMemory", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onLowMemory");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Toast.makeText(this, TAG + ": onTrimMemory", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onTrimMemory");
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Toast.makeText(this, TAG + ": onConfigurationChanged", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onTerminate() {
        Toast.makeText(this, TAG + ": onTerminate", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }
}
