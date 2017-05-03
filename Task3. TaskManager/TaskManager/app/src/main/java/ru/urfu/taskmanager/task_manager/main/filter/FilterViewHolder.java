package ru.urfu.taskmanager.task_manager.main.filter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import java.text.ParseException;
import java.util.Date;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.color_picker.recent.RecentColors;
import ru.urfu.taskmanager.utils.db.TasksDatabaseHelper;
import ru.urfu.taskmanager.utils.db.TasksFilter;
import ru.urfu.taskmanager.utils.tools.TimeUtils;


class FilterViewHolder {
    ListView savedFiltersList;
    Button filterApplyButton;
    Button filterSaveButton;

    private Context context;

    private RadioGroup dateRadioGroup, orderRadioGroup;
    private EditText startDateEditText, endDateEditText;
    private Spinner sortBySpinner;
    private Switch datePickerSwitcher, alphabeticallySwitcher, colorPickSwitcher;
    private View colorPickedArea;
    private View datePickerLayout;

    FilterViewHolder(View view) {
        this.context = view.getContext();

        initView(view);
        setupBehavior();
    }

    private void initView(View view) {
        savedFiltersList = (ListView) view.findViewById(R.id.saved_filters_list);

        alphabeticallySwitcher = (Switch) view.findViewById(R.id.alphabetically_switch);
        datePickerSwitcher = (Switch) view.findViewById(R.id.switch_date_picker);
        colorPickSwitcher = (Switch) view.findViewById(R.id.switch_color_picker);

        startDateEditText = (EditText) view.findViewById(R.id.start_date);
        endDateEditText = (EditText) view.findViewById(R.id.end_date);

        filterApplyButton = (Button) view.findViewById(R.id.filter_apply_button);
        filterSaveButton = (Button) view.findViewById(R.id.filter_save_button);

        orderRadioGroup = (RadioGroup) view.findViewById(R.id.order_radio_group);
        dateRadioGroup = (RadioGroup) view.findViewById(R.id.radio_date_picker);

        datePickerLayout = view.findViewById(R.id.date_picker_layout);
        colorPickedArea = view.findViewById(R.id.selected_color_area);

        sortBySpinner = (Spinner) view.findViewById(R.id.sort_by_spinner);
    }

    private void setupBehavior() {
        String dateString = TimeUtils.format(new Date());
        startDateEditText.setText(dateString);
        endDateEditText.setText(dateString);

        datePickerSwitcher.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked) {
                dateRadioGroup.setVisibility(View.VISIBLE);
                datePickerLayout.setVisibility(View.VISIBLE);
            } else {
                dateRadioGroup.setVisibility(View.GONE);
                datePickerLayout.setVisibility(View.GONE);
            }
        });

        dateRadioGroup.setOnCheckedChangeListener((group, checkedId) ->
        {
            switch (checkedId) {
                case R.id.by_single_date_radio:
                    endDateEditText.setEnabled(false);
                    break;
                case R.id.by_range_date_radio:
                    endDateEditText.setEnabled(true);
                    break;
            }
        });

        colorPickSwitcher.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked) {
                colorPickedArea.setAlpha(1f);
                RecentColors.showRecent(context,
                        color -> colorPickedArea.setBackgroundColor(color));
            } else {
                colorPickedArea.setAlpha(0.3f);
            }
        });

        colorPickedArea.setOnClickListener(v ->
        {
            if (colorPickSwitcher.isChecked()) {
                RecentColors.showRecent(context, v::setBackgroundColor);
            }
        });
    }

    TasksFilter.Builder compileFilterBuilder() {
        TasksFilter.Builder builder = TasksFilter.builder();

        switch (sortBySpinner.getSelectedItemPosition()) {
            case 0:
                builder.sortBy(TasksDatabaseHelper.TTL);
                break;
            case 1:
                builder.sortBy(TasksDatabaseHelper.TIME_CREATED);
                break;
            case 2:
                builder.sortBy(TasksDatabaseHelper.TIME_EDITED);
                break;
        }


        if (datePickerSwitcher.isChecked()) {
            try {
                Date startDate = TimeUtils.parse(startDateEditText.getText().toString());
                if (endDateEditText.isEnabled()) {
                    Date endDate = TimeUtils.parse(endDateEditText.getText().toString());
                    builder.fromDateRange(startDate.getTime(), endDate.getTime());
                } else {
                    builder.fromDate(startDate.getTime());
                }
            } catch (ParseException ignored) {
            }
        }

        if (colorPickSwitcher.isChecked()) {
            builder.fromColor(((ColorDrawable) colorPickedArea.getBackground()).getColor());
        }

        if (alphabeticallySwitcher.isChecked()) {
            builder.sortBy(TasksDatabaseHelper.TITLE);
        }

        switch (orderRadioGroup.getCheckedRadioButtonId()) {
            case R.id.order_front_radio:
                builder.setOrientation(TasksFilter.FRONT);
                break;
            case R.id.order_reverse_radio:
                builder.setOrientation(TasksFilter.REVERSE);
                break;
        }

        return builder;
    }
}
