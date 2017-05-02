package ru.urfu.taskmanager.task_manager.fragments.view;

import android.content.Intent;
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
import ru.urfu.taskmanager.task_manager.task_editor.view.TaskEditorActivity;
import ru.urfu.taskmanager.utils.db.TasksDatabaseHelper;
import ru.urfu.taskmanager.utils.db.TasksFilter;

import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.ACTION_EDIT;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_EDIT;

public abstract class TaskListFragment extends Fragment
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, TaskListView
{
    protected TaskManagerPresenter presenter;

    ListView tasksListView;
    TasksListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_tasks_fragment, container, false);
        return initView(view);
    }

    protected View initView(View root)
    {
        tasksListView = (ListView) root.findViewById(R.id.task_list);
        tasksListView.setAdapter(adapter = getAdapter());
        tasksListView.setOnItemClickListener(this);
        tasksListView.setOnItemLongClickListener(this);

        return root;
    }

    protected abstract TasksListAdapter getAdapter();

    public abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), TaskEditorActivity.class);
        intent.setAction(ACTION_EDIT);
        intent.putExtra(TasksDatabaseHelper.ID, (int) id);
        startActivityForResult(intent, REQUEST_EDIT);
        return true;
    }

    @Override
    public void onUpdate(TasksFilter.Builder filter) {
        adapter.updateData(filter);
    }

    @Override
    public Fragment getInstance() {
        return this;
    }

    @Override
    public TaskListView bindPresenter(TaskManagerPresenter presenter) {
        this.presenter = presenter;
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onResult(requestCode, resultCode, data);
    }

    @Override
    public void showAlert(String message) {
        Snackbar.make(getActivity().getWindow().getDecorView(), message, 2000).show();
    }
}
