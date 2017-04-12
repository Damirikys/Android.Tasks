package ru.urfu.colorpicker.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.urfu.colorpicker.R;
import ru.urfu.colorpicker.adapters.FavoriteListAdapter;
import ru.urfu.colorpicker.color_picker.PickerView;
import ru.urfu.colorpicker.color_picker.PickerViewStateListener;

public class MainActivity extends AppCompatActivity implements PickerViewStateListener
{
    private static final String STORAGE_NAME = "FavoriteColors";

    Vibrator vibrator;
    SharedPreferences storage;

    @BindView(R.id.picker_view)
    PickerView pickerContent;
    @BindView(R.id.current_color)
    ImageView currentColor;

    @BindView(R.id.rgb_value)
    EditText rgb_output;
    @BindView(R.id.hsv_value)
    EditText hsv_output;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    RecyclerView favoriteList;
    FavoriteListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        favoriteList = (RecyclerView) navigationView.getHeaderView(0)
                .findViewById(R.id.favorite_list);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        storage = getSharedPreferences(STORAGE_NAME, MODE_PRIVATE);

        List<Integer> favoriteColors = new ArrayList<>();
        Map<String, ?> map = storage.getAll();
        for (Object o : map.values())
            favoriteColors.add((int) o);

        favoriteList.setLayoutManager(new LinearLayoutManager(this));
        favoriteList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        favoriteList.setAdapter(adapter = new FavoriteListAdapter(favoriteColors));

        pickerContent.subscribe(this);
        pickerContent.setCellCount(16);
    }

    @OnClick(R.id.save_button)
    public void saveToFavorite() {
        String currentColor = String.valueOf(pickerContent.getCurrentColor());

        if (storage.getAll().containsKey(currentColor)) {
            Toast.makeText(this, "Вы уже добавили в избранное этот цвет", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = storage.edit();
        editor.putInt(currentColor, pickerContent.getCurrentColor());
        editor.apply();
        editor.commit();

        adapter.addItem(pickerContent.getCurrentColor());
        Toast.makeText(this, "Цвет сохранен в избранное", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onColorChanged(int color) {
        currentColor.setBackgroundColor(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        double d = Math.pow(10, 2);

        String hsv_value = String.valueOf(Math.round(hsv[0] * d) / d) + ", " +
                String.valueOf(Math.round(hsv[1] * d) / d) + ", " +
                String.valueOf(Math.round(hsv[2] * d) / d);

        String rgb_value = String.valueOf(Color.red(color)) + ", " +
                String.valueOf(Color.green(color)) + ", " +
                String.valueOf(Color.blue(color));

        hsv_output.setText(hsv_value);
        rgb_output.setText(rgb_value);
    }

    @OnClick(R.id.rgb_value)
    public void rgbCopyToBuffer() {
        copyToBuffer("RGB: " + rgb_output.getText().toString());
    }

    @OnClick(R.id.hsv_value)
    public void hsvCopyToBuffer() {
        copyToBuffer("HSV: " + rgb_output.getText().toString());
    }

    private void copyToBuffer(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Скопировано", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void editModeEnable() {
        vibrator.vibrate(10);
    }

    @Override
    public void editModeDisable() {
        vibrator.vibrate(10);
    }
}
