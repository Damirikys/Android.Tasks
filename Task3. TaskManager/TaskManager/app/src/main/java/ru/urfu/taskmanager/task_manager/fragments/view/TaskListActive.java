package ru.urfu.taskmanager.task_manager.fragments.view;

import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import java.util.Date;
import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.fragments.adapters.TasksListAdapter;

public class TaskListActive extends TaskListFragment
{
    @Override
    protected TasksListAdapter getAdapter() {
        return new TasksListAdapter(getContext(), TasksListAdapter.ACTIVE_TASKS);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.active_task_option_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId())
            {
                case R.id.task_is_complete:
                    presenter.taskIsCompleted((int) id);
                    break;
                case R.id.edit_the_task:
                    presenter.editTheTask((int) id);
                    break;
                case R.id.postpone_the_task:
                {
                    presenter.postponeTheTask(
                            (int) id, (date, entry) ->
                                    new SingleDateAndTimePickerDialog.Builder(getContext())
                                            .mainColor(getResources().getColor(R.color.colorAccent))
                                            .defaultDate(new Date(entry.getTtl()))
                                            .listener(date::call)
                                            .mustBeOnFuture()
                                            .bottomSheet()
                                            .build()
                                            .display()
                    );
                } break;
                case R.id.delete_the_task:
                    presenter.deleteTheTask((int) id);
                    break;
            }
            return true;
        });

        popup.setGravity(Gravity.END);
        popup.show();
    }
}
