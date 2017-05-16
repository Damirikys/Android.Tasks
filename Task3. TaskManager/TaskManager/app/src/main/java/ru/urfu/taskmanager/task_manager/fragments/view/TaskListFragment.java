package ru.urfu.taskmanager.task_manager.fragments.view;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.fragments.adapters.TasksListAdapter;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenter;

public abstract class TaskListFragment extends Fragment
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, TaskListView
{
    protected TaskManagerPresenter mPresenter;

    ListView mTaskListView;
    TasksListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_tasks_fragment, container, false);
        return initView(view);
    }

    protected View initView(View root) {
        mTaskListView = (ListView) root.findViewById(R.id.task_list);
        mTaskListView.setAdapter(mAdapter = getAdapter());
        mTaskListView.setOnItemClickListener(this);
        mTaskListView.setOnItemLongClickListener(this);

        return root;
    }

    protected abstract TasksListAdapter getAdapter();

    public abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.editTheTask((int) id);
        return true;
    }

    @Override
    public void onUpdate(Cursor... cursor) {
        getActivity().runOnUiThread(() -> mAdapter.updateData(cursor));
    }

    @Override
    public Fragment getInstance() {
        return this;
    }

    @Override
    public TaskListView bindPresenter(TaskManagerPresenter presenter) {
        this.mPresenter = presenter;
        return this;
    }

    @Override
    public int getDataType() {
        return mAdapter.getDataType();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onResult(requestCode, resultCode, data);
    }

    @Override
    public void showAlert(String message) {
        Snackbar.make(getActivity().getWindow().getDecorView(), message, 2000).show();
    }
}
