package ru.urfu.taskmanager.task_manager.editor.view;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.color_picker.PickerView;
import ru.urfu.taskmanager.color_picker.recent.RecentColors;
import ru.urfu.taskmanager.task_manager.editor.presenter.TaskEditorPresenter;
import ru.urfu.taskmanager.task_manager.editor.presenter.TaskEditorPresenterImpl;
import ru.urfu.taskmanager.task_manager.editor.tools.ImageLoader;
import ru.urfu.taskmanager.task_manager.main.view.TaskManager;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.utils.tools.TimeUtils;

import static android.content.Context.VIBRATOR_SERVICE;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_CREATE;
import static ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity.REQUEST_EDIT;

public class TaskEditorFragment extends Fragment implements TaskEditor, SingleDateAndTimePicker.Listener
{
    public static final int NON_ID = -1;
    public static final String EDITED_ITEM_ID_KEY = "ru.urfu.taskmanager.task_manager.editor.EDITED_ITEM_ID";
    public static final String TRANSITION_NAME = "transitionName";

    private static final int CELL_COUNT = 16;

    TaskManager mManager;
    TaskEditorPresenter mPresenter;

    SingleDateAndTimePicker mDateTimePicker;
    TextInputLayout mTitleInputLayout;
    TextInputLayout mDescInputLayout;
    ImageView mImageView;
    PickerView mPickerView;
    TextView mColorDeadlineTextView;
    EditText mTitleEditField;
    EditText mDescEditField;
    EditText mImageUrlEditField;
    Button mButtonSave;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater.inflate(R.layout.activity_task_editor, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    private View initView(View view) {
        mDateTimePicker = (SingleDateAndTimePicker) view.findViewById(R.id.datetime_picker);
        mColorDeadlineTextView = (TextView) view.findViewById(R.id.color_deadline);
        mButtonSave = (Button) view.findViewById(R.id.save_button);
        mDescEditField = (EditText) view.findViewById(R.id.descr_edit_field);
        mDescInputLayout = (TextInputLayout) view.findViewById(R.id.description_input_layout);
        mTitleEditField = (EditText) view.findViewById(R.id.title_edit_field);
        mTitleInputLayout = (TextInputLayout) view.findViewById(R.id.title_input_layout);
        mImageUrlEditField = (EditText) view.findViewById(R.id.image_url_edit_field);
        mImageView = (ImageView) view.findViewById(R.id.image_view);
        mPickerView = (PickerView) view.findViewById(R.id.pickerView);

        mImageUrlEditField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onImageLoad(s.toString());
            }
        });

        ViewCompat.setTransitionName(mColorDeadlineTextView, getArguments().getString(TRANSITION_NAME));

        return view;
    }

    @Override
    public int getEditedItemId() {
        Integer valueId = (Integer) getArguments().getSerializable(EDITED_ITEM_ID_KEY);
        return (valueId != null) ? valueId : NON_ID;
    }

    private void initialize() {
        mManager = (TaskManager) getActivity();

        mDateTimePicker.setMustBeOnFuture(true);
        mDateTimePicker.setListener(this);

        mButtonSave.setOnClickListener(this);
        mPickerView.setCellCount(CELL_COUNT);
        mPickerView.subscribe(this);

        onColorChanged(mPickerView.getCurrentColor());
        mColorDeadlineTextView.setOnClickListener(v -> RecentColors.showRecent(getContext(), color -> {
            mPickerView.setCurrentColor(color);
            onColorChanged(color);
        }));

        mPresenter = new TaskEditorPresenterImpl(this);
    }

    public void initializeEditor(TaskEntry entry) {
        getActivity().runOnUiThread(() -> {
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(entry.getTtlTimestamp());

            mDateTimePicker.selectDate(calendar);
            mDateTimePicker.setSelectorColor(Color.BLACK);
            mTitleEditField.setText(entry.getTitle());
            mDescEditField.setText(entry.getDescription());
            mImageUrlEditField.setText(entry.getImageUrl());
            mPickerView.setCurrentColor(entry.getColorInt());
            mColorDeadlineTextView.setText(TimeUtils.getHoursAndMinutesFromUnix(entry.getTtlTimestamp()).toString());
            onColorChanged(mPickerView.getCurrentColor());
        });
    }

    @Override
    public void onImageLoad(String url) {
        mImageView.post(() -> ImageLoader.into(mImageView)
                .from(url));
    }

    @Override
    public void showTitleError(String string) {
        mTitleInputLayout.setError(string);
    }

    @Override
    public void showDescriptionError(String string) {
        mDescInputLayout.setError(string);
    }

    @Override
    public void onClick(View v) {
        mTitleInputLayout.setErrorEnabled(false);
        mDescInputLayout.setErrorEnabled(false);

        mPresenter.saveState(
                new TaskEntry()
                        .setTitle(mTitleEditField.getText().toString())
                        .setDescription(mDescEditField.getText().toString())
                        .setTtl(mDateTimePicker.getDate().getTime())
                        .setColor(mPickerView.getCurrentColor())
                        .setImageUrl(mImageUrlEditField.getText().toString())
        );
    }

    @Override
    public void onColorChanged(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(100f);

        mColorDeadlineTextView.setBackground(gd);
    }

    @Override
    public void editModeEnable() {
        vibrate();
    }

    @Override
    public void editModeDisable() {
        vibrate();
    }

    @Override
    public void theBoundaryIsReached() {
        vibrate();
    }

    @Override
    public void exit(int result) {
        mManager.getPresenter()
                .onResult((getEditedItemId() == NON_ID) ? REQUEST_CREATE : REQUEST_EDIT);

        mManager.getSupportFragmentManager()
                .popBackStack();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(10);
    }

    @Override
    public void onDateChanged(String displayed, Date date) {
        mColorDeadlineTextView.setText(TimeUtils.getHoursAndMinutesFromUnix(date.getTime()).toString());
    }

    public static TaskEditorFragment newInstance(@Nullable Bundle args) {
        TaskEditorFragment fragment = new TaskEditorFragment();
        if (args != null) fragment.setArguments(args);
        return fragment;
    }
}
