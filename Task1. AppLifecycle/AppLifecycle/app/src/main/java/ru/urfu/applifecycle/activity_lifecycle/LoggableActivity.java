package ru.urfu.applifecycle.activity_lifecycle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import ru.urfu.applifecycle.interfaces.Loggable;
import ru.urfu.applifecycle.utils.TimeUtils;

public abstract class LoggableActivity extends AppCompatActivity implements Loggable
{
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Activity was created", Toast.LENGTH_SHORT).show();
        log("onCreate");
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        log("onPostCreate");
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        Toast.makeText(this, "Activity is visible", Toast.LENGTH_SHORT).show();
        log("onResume");
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        log("onPostResume");
        super.onPostResume();
    }

    @Override
    protected void onStart() {
        Toast.makeText(this, "Activity was started", Toast.LENGTH_SHORT).show();
        log("onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "Activity was hidden", Toast.LENGTH_SHORT).show();
        log("onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "Activity restart", Toast.LENGTH_SHORT).show();
        log("onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "Activity was stopped", Toast.LENGTH_SHORT).show();
        log("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Activity was destroyed", Toast.LENGTH_SHORT).show();
        log("onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        log("onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        log("onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void log(String body) {
        String tag = TimeUtils.getTimeRepresentationFromUnix(
                System.currentTimeMillis()) + " | " + TAG;

        log(tag, body);
    }

    public void log(String tag, String body) {
        Log.d(tag, body);
    }
}
