package ru.urfu.taskmanager.task_manager.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.main.fragments.view.TaskListActive;
import ru.urfu.taskmanager.task_manager.main.fragments.view.TaskListCompleted;
import ru.urfu.taskmanager.task_manager.main.view.TaskManager;
import ru.urfu.taskmanager.task_manager.main.fragments.adapters.TaskPagerAdapter;

public class TaskPagerFragment extends Fragment implements View.OnClickListener
{
    private TaskManager mManager;
    private FragmentStatePagerAdapter mPagerAdapter;

    protected ViewPager mViewPager;
    protected TabLayout mTabLayout;

    protected FloatingActionButton fab;

    public TaskPagerFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initView(inflater.inflate(R.layout.main_fragment, container, false));
    }

    private View initView(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mManager = (TaskManager) getActivity();
        mPagerAdapter = getPagerAdapter();
        configViewPager();
        mTabLayout.setupWithViewPager(mViewPager);
    }

    protected void configViewPager() {
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mPagerAdapter);
    }

    protected FragmentStatePagerAdapter getPagerAdapter() {
        return new TaskPagerAdapter(getChildFragmentManager())
                .addPage(TaskListActive.newInstance(getString(R.string.active_tasks_title)))
                .addPage(TaskListCompleted.newInstance(getString(R.string.completed_tasks_title)));
    }

    public static Fragment newInstance(Bundle args) {
        TaskPagerFragment fragment = new TaskPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        mManager.startEditor(null, null, null);
    }
}
