package ru.urfu.applifecycle.activity_lifecycle;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.urfu.applifecycle.R;
import ru.urfu.applifecycle.utils.TimeUtils;
import ru.urfu.applifecycle.view_lifecycle.ViewLifecycleActivity;

public class LifecycleActivity extends LoggableActivity
{
    private static final String LOG_BODY = "log_body";

    @BindView(R.id.log_content) TextView log_body;
    @BindView(R.id.btn_log_clear) Button btn_log_clear;
    @BindView(R.id.button_two) Button button_two;
    @BindView(R.id.button_three) Button button_three;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_lifecycle);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        log_body.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence(LOG_BODY, log_body.getText());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        log_body.setText(savedInstanceState.getCharSequence(LOG_BODY));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @OnClick(R.id.btn_log_clear)
    public void onClearLogBody() {
        log_body.setText(null);
        Toast.makeText(this, "Logs cleared!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_two)
    public void onStartNewActivity() {
        Intent intent = new Intent(this, ViewLifecycleActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button_three)
    public void onFinish() {
        finish();
    }

    @Override
    public void log(String tag, String body) {
        super.log(tag, body);
        log_body.append(tag + ": " + body + "\n");
    }
}
