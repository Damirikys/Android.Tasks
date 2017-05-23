package ru.urfu.taskmanager.task_manager.main.fragments.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ru.urfu.taskmanager.task_manager.main.fragments.view.TaskListFragment;

public class TaskPagerAdapter extends FragmentStatePagerAdapter
{
    private List<TaskListFragment> fragments;

    public TaskPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<>();
    }

    public TaskPagerAdapter addPage(TaskListFragment fragment) {
        fragments.add(fragment);
        return this;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
