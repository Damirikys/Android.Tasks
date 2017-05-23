package ru.urfu.taskmanager.task_manager.main.fragments.view;

import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.util.Date;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.main.fragments.adapters.TasksListAdapter;
import ru.urfu.taskmanager.data.db.DbTasksHelper;
import ru.urfu.taskmanager.data.db.DbTasksFilter;

public class TaskListActive extends TaskListFragment
{
    private DbTasksFilter filter;

    public static TaskListActive newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE_KEY, title);

        TaskListActive fragment = new TaskListActive();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected TasksListAdapter getAdapter() {
        mManager.getFilterLayoutWrapper().onCompileFilter(builder -> filter = builder.setType(DbTasksFilter.ACTIVE_TASK)
                .sortBy(DbTasksHelper.TTL)
                .build());

        return new TasksListAdapter(this, filter);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onItemClick(TasksListAdapter.ViewHolder holder, int position, long id) {
        PopupMenu popup = new PopupMenu(getContext(), holder.layout);
        popup.getMenuInflater().inflate(R.menu.active_task_option_menu, popup.getMenu());
        popup.setGravity(Gravity.END);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.task_is_complete:
                    mManager.getPresenter().taskIsCompleted((int) id);
                    break;
                case R.id.edit_the_task:
                    mManager.getPresenter().editTheTask(position, mAdapter, holder);
                    break;
                case R.id.postpone_the_task: {
                    mManager.getPresenter().postponeTheTask(
                            (int) id, (date, entry) ->
                                    new SingleDateAndTimePickerDialog.Builder(getContext())
                                            .mainColor(getResources().getColor(R.color.colorAccent))
                                            .defaultDate(new Date(entry.getTtlTimestamp()))
                                            .listener(date::call)
                                            .mustBeOnFuture()
                                            .bottomSheet()
                                            .build()
                                            .display()
                    );
                }
                break;
                case R.id.delete_the_task:
                    mManager.getPresenter().deleteTheTask((int) id);
                    break;
            }
            return true;
        });

        popup.show();
    }
}
