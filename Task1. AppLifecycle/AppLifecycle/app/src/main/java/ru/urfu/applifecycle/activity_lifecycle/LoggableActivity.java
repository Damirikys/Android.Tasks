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
        super.onPostCreate(savedInstanceState);
        log("onPostCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "Activity is visible", Toast.LENGTH_SHORT).show();
        log("onResume");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        log("onPostResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Activity was started", Toast.LENGTH_SHORT).show();
        log("onStart");
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
        super.onSaveInstanceState(outState);
        log("onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        log("onRestoreInstanceState");
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
