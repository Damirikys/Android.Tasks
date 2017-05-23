package ru.urfu.taskmanager.task_manager.main.fragments.view;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.data.db.DbFilter;
import ru.urfu.taskmanager.task_manager.main.fragments.adapters.TasksListAdapter;
import ru.urfu.taskmanager.task_manager.main.fragments.helper.OnStartDragListener;
import ru.urfu.taskmanager.task_manager.main.fragments.helper.SimpleItemTouchHelperCallback;
import ru.urfu.taskmanager.task_manager.main.view.TaskManager;

public abstract class TaskListFragment extends Fragment
        implements TaskListView, OnStartDragListener
{
    public static final String ARG_TITLE_KEY = "ru.urfu.taskmanager.task_manager.ARG_TITLE_KEY";
    private ItemTouchHelper mItemTouchHelper;
    protected TaskManager mManager;

    RecyclerView mTaskListView;
    TasksListAdapter mAdapter;

    @Override
    public String getTitle() {
        return getArguments().getString(ARG_TITLE_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_tasks_fragment, container, false);
        return initView(view);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mManager = (TaskManager) getActivity();
        mManager.getPresenter().bindView(this);

        mTaskListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTaskListView.setAdapter(mAdapter = getAdapter());

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mTaskListView);
    }

    protected View initView(View root) {
        mTaskListView = (RecyclerView) root.findViewById(R.id.task_list);
        return root;
    }

    protected abstract TasksListAdapter getAdapter();

    public abstract void onItemClick(TasksListAdapter.ViewHolder holder, int position, long id);

    public boolean onItemLongClick(TasksListAdapter.ViewHolder holder, int position, long id) {
        mManager.startEditor(position, mAdapter, holder);
        return true;
    }

    @Override
    public void onUpdate(Cursor... cursor) {
        getActivity().runOnUiThread(() -> mAdapter.updateData(cursor));
    }

    @Override
    public void onUpdate(DbFilter filter) {
        getActivity().runOnUiThread(() -> mAdapter.updateFilter(filter));
    }

    @Override
    public Fragment getInstance() {
        return this;
    }

    @Override
    public int getDataType() {
        return mAdapter.getDataType();
    }

    @Override
    public void showAlert(String message) {
        Snackbar.make(getActivity().getWindow().getDecorView(), message, 2000).show();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mManager.getPresenter().unBindView(this);
    }
}
