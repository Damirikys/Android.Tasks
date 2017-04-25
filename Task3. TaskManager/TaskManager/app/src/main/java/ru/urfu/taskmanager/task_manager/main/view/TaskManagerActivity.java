package ru.urfu.taskmanager.task_manager.main.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.task_editor.view.TaskEditorActivity;
import ru.urfu.taskmanager.task_manager.main.adapters.ViewPagerAdapter;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListActive;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListCompleted;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenter;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenterImpl;

public class TaskManagerActivity extends AppCompatActivity implements TaskManager
{
    public static final String ACTION_CREATE = "ru.urfu.taskmanager.ACTION_CREATE";
    public static final String ACTION_EDIT = "ru.urfu.taskmanager.ACTION_EDIT";

    public static final int REQUEST_CREATE = 1;
    public static final int REQUEST_EDIT = 2;

    TaskManagerPresenter presenter;
    FloatingActionButton actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        presenter = new TaskManagerPresenterImpl(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionButton = (FloatingActionButton) findViewById(R.id.fab);
        actionButton.setOnClickListener(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.add(presenter.bindView(new TaskListActive()), getString(R.string.active_tasks_title));
        adapter.add(presenter.bindView(new TaskListCompleted()), getString(R.string.completed_tasks_title));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, TaskEditorActivity.class);
        intent.setAction(ACTION_CREATE);
        startActivityForResult(intent, REQUEST_CREATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onResult(requestCode, resultCode);
    }

    @Override
    public void showAlert(String message) {
        Snackbar.make(getWindow().getDecorView(), message, 2000).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
