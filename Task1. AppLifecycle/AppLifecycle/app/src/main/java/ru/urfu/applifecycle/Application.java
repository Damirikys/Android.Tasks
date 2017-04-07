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
        super.onLowMemory();
        Toast.makeText(this, TAG + ": onLowMemory", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Toast.makeText(this, TAG + ": onTrimMemory", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onTrimMemory");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(this, TAG + ": onConfigurationChanged", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Toast.makeText(this, TAG + ": onTerminate", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onTerminate");
    }
}
