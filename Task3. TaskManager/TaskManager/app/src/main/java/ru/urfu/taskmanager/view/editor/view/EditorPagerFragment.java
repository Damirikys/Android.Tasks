package ru.urfu.taskmanager.view.editor.view;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import ru.urfu.taskmanager.db.DbTasks;
import ru.urfu.taskmanager.db.filter.DbTasksFilter;
import ru.urfu.taskmanager.view.main.fragments.helper.CursorProvider;
import ru.urfu.taskmanager.view.main.fragments.TaskPagerFragment;
import ru.urfu.taskmanager.entities.TaskEntry;

import static ru.urfu.taskmanager.view.editor.view.TaskEditorFragment.EDITED_ITEM_ID_KEY;
import static ru.urfu.taskmanager.view.editor.view.TaskEditorFragment.TRANSITION_NAME;

public class EditorPagerFragment extends TaskPagerFragment
{
    public static final String EDITOR_PAGER_POSITION = "ru.urfu.taskmanager.view.editor.EDITOR_PAGER_POSITION";

    private CursorProvider mDataAdapter;
    private DbTasks mDatabase;

    public EditorPagerFragment() {
        mDatabase = DbTasks.getInstance();
    }

    public EditorPagerFragment bindCursorProvider(CursorProvider provider) {
        mDataAdapter = provider;
        return this;
    }

    @Override
    protected void configViewPager() {
        super.configViewPager();
        Integer position = (Integer) getArguments().getSerializable(EDITOR_PAGER_POSITION);
        position = (position != null) ? position : 0;
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mViewPager.setCurrentItem(position);
        fab.setVisibility(View.GONE);
    }

    @Override
    protected FragmentStatePagerAdapter getPagerAdapter() {
        return new EditorPagerAdapter(getChildFragmentManager());
    }

    public static EditorPagerFragment newInstance(Bundle args) {
        EditorPagerFragment fragment = new EditorPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private class EditorPagerAdapter extends FragmentStatePagerAdapter
    {
        private Cursor cursor;

        EditorPagerAdapter(FragmentManager fm) {
            super(fm);
            cursor = (mDataAdapter != null) ? mDataAdapter.getCursor() : mDatabase
                    .getCursor(DbTasksFilter.DEFAULT_BUILDER.build());
        }

        @Override
        public Fragment getItem(int position) {
            int editedItemId = getEntryFromPosition(position)
                    .getId();

            Bundle bundle = new Bundle();
            bundle.putInt(EDITED_ITEM_ID_KEY, editedItemId);
            bundle.putString(TRANSITION_NAME, "timeBlock_" + position);
            return TaskEditorFragment.newInstance(bundle);
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getEntryFromPosition(position).getTitle();
        }

        private TaskEntry getEntryFromPosition(int position) {
            cursor.moveToPosition(position);
            return mDatabase.getCurrentEntryFromCursor(cursor);
        }
    }
}
