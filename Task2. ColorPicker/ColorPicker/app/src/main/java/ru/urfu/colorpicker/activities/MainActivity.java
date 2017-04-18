package ru.urfu.colorpicker.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.urfu.colorpicker.Application;
import ru.urfu.colorpicker.R;
import ru.urfu.colorpicker.adapters.FavoriteListAdapter;
import ru.urfu.colorpicker.color_picker.PickerView;
import ru.urfu.colorpicker.color_picker.listeners.PickerViewStateListener;

public class MainActivity extends AppCompatActivity implements PickerViewStateListener
{
    private final Resources resources = Application.getContext().getResources();
    private final String TITLE = resources.getString(R.string.app_name);
    private final String SUBTITLE = resources.getString(R.string.app_desc);
    private final String STORAGE_NAME = resources.getString(R.string.color_storage_name);
    private static final int CELL_COUNT = 16;

    private Vibrator vibrator;
    private SharedPreferences storage;

    @BindView(R.id.content_body)
    LinearLayout contentLayout;

    @BindView(R.id.picker_view)
    PickerView pickerContent;
    @BindView(R.id.current_color)
    ImageView currentColor;

    @BindView(R.id.rgb_value)
    EditText rgb_output;
    @BindView(R.id.hsv_value)
    EditText hsv_output;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.included_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_menu_list);
        toolbar.setTitle(TITLE);
        toolbar.setSubtitle(SUBTITLE);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        favoriteList = (RecyclerView) navigationView.getHeaderView(0)
                .findViewById(R.id.favorite_list);
        NestedScrollView scrollWrapper = (NestedScrollView) navigationView.getHeaderView(0)
                .findViewById(R.id.scroll_wrapper);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        storage = getSharedPreferences(STORAGE_NAME, MODE_PRIVATE);

        List<Integer> favoriteColors = new ArrayList<>();
        Map<String, ?> map = storage.getAll();
        for (Object o : map.values())
            favoriteColors.add((int) o);

        favoriteList.setNestedScrollingEnabled(false);
        favoriteList.setLayoutManager(new LinearLayoutManager(this));
        favoriteList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        favoriteList.setAdapter(adapter = new FavoriteListAdapter(new FavoriteListAdapter.EventListener() {
            @Override
            public void onScrollingEnable() {
                scrollWrapper.requestDisallowInterceptTouchEvent(false);
                favoriteList.requestDisallowInterceptTouchEvent(false);
            }

            @Override
            public void onScrollingDisable() {
                scrollWrapper.requestDisallowInterceptTouchEvent(true);
                favoriteList.requestDisallowInterceptTouchEvent(true);
            }

            @Override
            public void onColorSelected(int color) {
                pickerContent.setCurrentColor(color);
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onColorDeleted(int color) {
                deleteFromFavorite(color);
            }
        }, favoriteColors));


        currentColor.setBackgroundColor(pickerContent.getCurrentColor());
        pickerContent.subscribe(this);
        pickerContent.setCellCount(CELL_COUNT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            contentLayout.setOrientation(LinearLayout.VERTICAL);
        }
    }

    @OnClick(R.id.save_button)
    public void saveToFavorite() {
        String currentColor = String.valueOf(pickerContent.getCurrentColor());

        if (storage.getAll().containsKey(currentColor)) {
            Toast.makeText(this, resources.getString(R.string.already_in_favorite), Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = storage.edit();
        editor.putInt(currentColor, pickerContent.getCurrentColor());
        editor.apply();
        editor.commit();

        adapter.addItem(pickerContent.getCurrentColor());
        Toast.makeText(this, resources.getString(R.string.saved_in_favorite), Toast.LENGTH_SHORT).show();
    }

    public void deleteFromFavorite(int color) {
        SharedPreferences.Editor editor = storage.edit();
        editor.remove(String.valueOf(color));
        editor.apply();
        editor.commit();
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
        copyToBuffer(resources.getString(R.string.rgb) + rgb_output.getText().toString());
    }

    @OnClick(R.id.hsv_value)
    public void hsvCopyToBuffer() {
        copyToBuffer(resources.getString(R.string.hsv) + rgb_output.getText().toString());
    }

    private void copyToBuffer(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, resources.getString(R.string.copied), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void editModeEnable() {
        vibrator.vibrate(10);
    }

    @Override
    public void editModeDisable() {
        vibrator.vibrate(10);
    }

    @Override
    public void theBoundaryIsReached() {
        vibrator.vibrate(10);
    }
}
