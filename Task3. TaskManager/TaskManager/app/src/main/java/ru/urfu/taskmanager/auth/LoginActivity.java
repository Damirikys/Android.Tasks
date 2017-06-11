package ru.urfu.taskmanager.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.entities.User;
import ru.urfu.taskmanager.view.main.view.TaskManagerActivity_;

@SuppressLint("Registered")
@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity
{
    @ViewById(R.id.loginEditText)
    EditText mLoginEditText;

    @AfterViews
    public void init() {
        if (User.getActiveUser() != null) {
            afterLogin();
        }
    }

    @Click(R.id.loginButton)
    public void doLogin() {
        String login = mLoginEditText.getText().toString();
        if (!login.isEmpty()) {
            User.doLogin(getApplicationContext(), login);
            afterLogin();
        }
    }

    private void afterLogin() {
        Intent intent = new Intent(this, TaskManagerActivity_.class);
        startActivity(intent);

        finish();
    }
}
