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


class FilterViewHolder
{
    ListView mSavedFiltersList;
    Button mFilterApplyButton;
    Button mFilterSaveButton;

    private Context mContext;

    private RadioGroup mDateRadioGroup, mOrderRadioGroup;
    private EditText mStartDateEditText, mEndDateEditText;
    private Spinner mSortBySpinner;
    private Switch mDatePickerSwitcher, mAlphabeticallySwitcher, mColorPickSwitcher;
    private View mColorPickedArea, mDatePickerLayout;

    FilterViewHolder(View view) {
        this.mContext = view.getContext();

        initView(view);
        setupBehavior();
    }

    private void initView(View view) {
        mSavedFiltersList = (ListView) view.findViewById(R.id.saved_filters_list);

        mAlphabeticallySwitcher = (Switch) view.findViewById(R.id.alphabetically_switch);
        mDatePickerSwitcher = (Switch) view.findViewById(R.id.switch_date_picker);
        mColorPickSwitcher = (Switch) view.findViewById(R.id.switch_color_picker);

        mStartDateEditText = (EditText) view.findViewById(R.id.start_date);
        mEndDateEditText = (EditText) view.findViewById(R.id.end_date);

        mFilterApplyButton = (Button) view.findViewById(R.id.filter_apply_button);
        mFilterSaveButton = (Button) view.findViewById(R.id.filter_save_button);

        mOrderRadioGroup = (RadioGroup) view.findViewById(R.id.order_radio_group);
        mDateRadioGroup = (RadioGroup) view.findViewById(R.id.radio_date_picker);

        mDatePickerLayout = view.findViewById(R.id.date_picker_layout);
        mColorPickedArea = view.findViewById(R.id.selected_color_area);

        mSortBySpinner = (Spinner) view.findViewById(R.id.sort_by_spinner);
    }

    private void setupBehavior() {
        String dateString = TimeUtils.format(new Date());
        mStartDateEditText.setText(dateString);
        mEndDateEditText.setText(dateString);

        mDatePickerSwitcher.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked) {
                mDateRadioGroup.setVisibility(View.VISIBLE);
                mDatePickerLayout.setVisibility(View.VISIBLE);
            } else {
                mDateRadioGroup.setVisibility(View.GONE);
                mDatePickerLayout.setVisibility(View.GONE);
            }
        });

        mDateRadioGroup.setOnCheckedChangeListener((group, checkedId) ->
        {
            switch (checkedId) {
                case R.id.by_single_date_radio:
                    mEndDateEditText.setEnabled(false);
                    break;
                case R.id.by_range_date_radio:
                    mEndDateEditText.setEnabled(true);
                    break;
            }
        });

        mColorPickSwitcher.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if (isChecked) {
                mColorPickedArea.setAlpha(1f);
                RecentColors.showRecent(mContext,
                        color -> mColorPickedArea.setBackgroundColor(color));
            } else {
                mColorPickedArea.setAlpha(0.3f);
            }
        });

        mColorPickedArea.setOnClickListener(v ->
        {
            if (mColorPickSwitcher.isChecked()) {
                RecentColors.showRecent(mContext, v::setBackgroundColor);
            }
        });
    }

    TasksFilter.Builder compileFilterBuilder() {
        TasksFilter.Builder builder = TasksFilter.builder();

        switch (mSortBySpinner.getSelectedItemPosition()) {
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


        if (mDatePickerSwitcher.isChecked()) {
            try {
                Date startDate = TimeUtils.parse(mStartDateEditText.getText().toString());
                if (mEndDateEditText.isEnabled()) {
                    Date endDate = TimeUtils.parse(mEndDateEditText.getText().toString());
                    builder.fromDateRange(startDate.getTime(), endDate.getTime());
                } else {
                    builder.fromDate(startDate.getTime());
                }
            } catch (ParseException ignored) {
            }
        }

        if (mColorPickSwitcher.isChecked()) {
            builder.fromColor(((ColorDrawable) mColorPickedArea.getBackground()).getColor());
        }

        if (mAlphabeticallySwitcher.isChecked()) {
            builder.sortBy(TasksDatabaseHelper.TITLE);
        }

        switch (mOrderRadioGroup.getCheckedRadioButtonId()) {
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
