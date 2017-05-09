package ru.urfu.taskmanager.task_manager.fragments.view;

import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import java.util.Date;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.fragments.adapters.TasksListAdapter;
import ru.urfu.taskmanager.utils.db.DbTasksHelper;
import ru.urfu.taskmanager.utils.db.DbTasksFilter;

public class TaskListCompleted extends TaskListFragment
{
    @Override
    protected TasksListAdapter getAdapter() {
        return new TasksListAdapter(getContext(),
                DbTasksFilter.builder()
                        .setType(DbTasksFilter.COMPLETED_TASK)
                        .sortBy(DbTasksHelper.TTL)
                        .build()
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.completed_task_option_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.restore_the_task: {
                    mPresenter.restoreTheTask(
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
