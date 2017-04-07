package ru.urfu.applifecycle.view_lifecycle;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.urfu.applifecycle.R;
import ru.urfu.applifecycle.interfaces.Loggable;

public class ViewLifecycleActivity extends AppCompatActivity
{
    private final String TAG = this.getClass().getSimpleName();
    private final int delay = 2000;

    Handler handler;

    @BindView(R.id.view_lifecycle)
    RelativeLayout content;
    @BindView(R.id.viewgroup_log)
    TextView viewgroup_log;
    @BindView(R.id.view_log)
    TextView view_log;
    @BindView(R.id.view_lifecycle_status)
    TextView status;

    LoggableGroupView layout;
    LoggableView view;

    Loggable lifecycleListener = new Loggable() {
        @Override
        public void log(String tag, String body) {
            switch (tag) {
                case LoggableGroupView.TAG:
                    viewgroup_log.append(body + "\n");
                    break;
                case LoggableView.TAG:
                    view_log.append(body + "\n");
                    break;
                default:
                    status.setText(body);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_lifecycle);
        handler = new Handler(getMainLooper());
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoggableGroupView.setLifecycleLogger(lifecycleListener);
        LoggableView.setLifecycleLogger(lifecycleListener);

        startTest();
    }

    private void startTest()
    {
        new Thread(() -> {
            try {
                handler.post(() -> {
                    Log.d(TAG, "Create a RelativeLayout");
                    event("Create a RelativeLayout");
                    layout = new LoggableGroupView(ViewLifecycleActivity.this);
                });

                Thread.sleep(delay);

                handler.post(() -> {
                    Log.d(TAG, "Attach a RelativeLayout to the window");
                    event("Attach a RelativeLayout to the window");
                    content.addView(layout);
                });

                Thread.sleep(delay);

                handler.post(() -> {
                    Log.d(TAG, "Create a TextView");
                    event("Create a TextView");
                    view = new LoggableView(ViewLifecycleActivity.this);
                });

                Thread.sleep(delay);

                handler.post(() -> {
                    Log.d(TAG, "Attach a TextView to the RelativeLayout");
                    event("Attach a TextView to the RelativeLayout");
                    layout.addView(view);
                });

                Thread.sleep(delay);

                handler.post(() -> {
                    Log.d(TAG, "Remove a TextView from RelativeLayout");
                    event("Remove a TextView from RelativeLayout");
                    layout.removeView(view);
                });

                Thread.sleep(delay);

                handler.post(() -> {
                    Log.d(TAG, "Remove a RelativeLayout from window");
                    event("Remove a RelativeLayout from window");
                    content.removeView(layout);
                });

                Thread.sleep(delay);

                handler.post(() -> event("The test is complete."));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void event(String text) {
        lifecycleListener.log("", text);
    }
}
