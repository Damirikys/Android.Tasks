package ru.urfu.taskmanager.task_manager.editor.view;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.color_picker.PickerView;
import ru.urfu.taskmanager.color_picker.recent.RecentColors;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.task_manager.editor.presenter.TaskEditorPresenter;
import ru.urfu.taskmanager.task_manager.editor.presenter.TaskEditorPresenterImpl;
import ru.urfu.taskmanager.task_manager.editor.tools.ImageLoader;

@EActivity(R.layout.activity_task_editor)
public class TaskEditorActivity extends AppCompatActivity implements TaskEditor
{
    private static final String CACHE_KEY = "color_cache";
    private static final String COLOR_KEY = "current_color";
    private static final String DATE_KEY = "selected_date";
    private static final int CELL_COUNT = 16;

    TaskEditorPresenter mPresenter;

    @ViewById(R.id.datetime_picker)
    SingleDateAndTimePicker mDateTimePicker;

    @ViewById(R.id.title_input_layout)
    TextInputLayout mTitleInputLayout;

    @ViewById(R.id.description_input_layout)
    TextInputLayout mDescInputLayout;

    @ViewById(R.id.image_view)
    ImageView mImageView;

    @ViewById(R.id.pickerView)
    PickerView mPickerView;

    @ViewById(R.id.cardColor)
    CardView mCardColorView;

    @ViewById(R.id.title_edit_field)
    EditText mTitleEditField;

    @ViewById(R.id.descr_edit_field)
    EditText mDescEditField;

    @ViewById(R.id.image_url_edit_field)
    EditText mImageUrlEditField;

    @ViewById(R.id.save_button)
    Button mButtonSave;

    boolean mRestored = false;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState != null) mRestored = true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.editor_create_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setResult(RESULT_CANCELED);

        mDateTimePicker.setMustBeOnFuture(true);
        mButtonSave.setOnClickListener(this);
        mPickerView.setCellCount(CELL_COUNT);
        mPickerView.subscribe(this);

        mCardColorView.setCardBackgroundColor(mPickerView.getCurrentColor());
        mCardColorView.setOnClickListener(v -> RecentColors.showRecent(this, color -> {
            mPickerView.setCurrentColor(color);
            mCardColorView.setCardBackgroundColor(color);
        }));

        mPresenter = new TaskEditorPresenterImpl(this);
    }

    public boolean isRestored() {
        return mRestored;
    }

    @UiThread
    public void initializeEditor(TaskEntry entry) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(entry.getTtlTimestamp());

        mDateTimePicker.selectDate(calendar);
        mDateTimePicker.setSelectorColor(Color.BLACK);
        mTitleEditField.setText(entry.getTitle());
        mDescEditField.setText(entry.getDescription());
        mImageUrlEditField.setText(entry.getImageUrl());
        mPickerView.setCurrentColor(entry.getColorInt());
        mCardColorView.setCardBackgroundColor(mPickerView.getCurrentColor());
    }

    @Override
    @UiThread
    public void onComplete(TaskEntry entry) {
        ImageLoader.into(mImageView)
                .from(entry.getImageUrl());
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
        mCardColorView.setCardBackgroundColor(color);
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

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(10);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mPickerView.getColorCache();
        outState.putSerializable(CACHE_KEY, mPickerView.getColorCache());
        outState.putInt(COLOR_KEY, mPickerView.getCurrentColor());
        outState.putSerializable(DATE_KEY, mDateTimePicker.getDate());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date) savedInstanceState.getSerializable(DATE_KEY));
        float[][] cache = (float[][]) savedInstanceState.getSerializable(CACHE_KEY);
        int currentColor = savedInstanceState.getInt(COLOR_KEY);
        mPickerView.setColorCache(cache);
        mPickerView.setCellCount(CELL_COUNT);
        mPickerView.setCurrentColor(currentColor);
        mCardColorView.setCardBackgroundColor(mPickerView.getCurrentColor());
        mDateTimePicker.selectDate(calendar);
    }

    @Override
    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void exit(int result) {
        setResult(result);
        finish();
    }
}
