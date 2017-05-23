package ru.urfu.taskmanager.task_manager.main.fragments.view;

import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.util.Date;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.data.db.DbTasksFilter;
import ru.urfu.taskmanager.data.db.DbTasksHelper;
import ru.urfu.taskmanager.task_manager.main.fragments.adapters.TasksListAdapter;

public class TaskListCompleted extends TaskListFragment
{
    private DbTasksFilter filter;

    public static TaskListCompleted newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE_KEY, title);

        TaskListCompleted fragment = new TaskListCompleted();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected TasksListAdapter getAdapter() {
        mManager.getFilterLayoutWrapper().onCompileFilter(builder -> filter = builder.setType(DbTasksFilter.COMPLETED_TASK)
                .sortBy(DbTasksHelper.TTL)
                .build());

        return new TasksListAdapter(this, filter);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onItemClick(TasksListAdapter.ViewHolder holder, int position, long id) {
        PopupMenu popup = new PopupMenu(getContext(), holder.layout);
        popup.getMenuInflater().inflate(R.menu.completed_task_option_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.restore_the_task: {
                    mManager.getPresenter().restoreTheTask(
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
            }

            return true;
        });

        popup.setGravity(Gravity.END);
        popup.show();
    }
}
