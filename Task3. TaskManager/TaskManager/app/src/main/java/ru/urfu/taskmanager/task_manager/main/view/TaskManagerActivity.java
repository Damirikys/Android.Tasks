package ru.urfu.taskmanager.task_manager.main.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.task_manager.main.filter.FiltersStorage;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListActive;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListCompleted;
import ru.urfu.taskmanager.task_manager.main.adapters.FiltersAdapter;
import ru.urfu.taskmanager.task_manager.main.adapters.PermissionsAdapter;
import ru.urfu.taskmanager.task_manager.main.adapters.SavedFiltersAdapter;
import ru.urfu.taskmanager.task_manager.main.adapters.ViewPagerAdapter;
import ru.urfu.taskmanager.task_manager.main.filter.FilterLayoutWrapper;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenter;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenterImpl;
import ru.urfu.taskmanager.task_manager.task_editor.view.TaskEditorActivity;
import ru.urfu.taskmanager.utils.tools.DirectoryChooser;
import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.utils.db.TasksDatabaseHelper;
import ru.urfu.taskmanager.utils.tools.JSONFactory;

public class TaskManagerActivity extends AppCompatActivity
        implements TaskManager, MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener
{
    public static final String ACTION_CREATE = "ru.urfu.taskmanager.ACTION_CREATE";
    public static final String ACTION_EDIT = "ru.urfu.taskmanager.ACTION_EDIT";

    public static final int REQUEST_CREATE = 1;
    public static final int REQUEST_EDIT = 2;
    public static final int REQUEST_IMPORT = 3;

    public static final String EXPORTED_FILE_NAME = "itemlist.ili";

    private TaskManagerPresenter presenter;

    private enum ToolbarMode {
        NORMAL, SEARCH, FILTER
    }

    private FilterLayoutWrapper filterLayoutWrapper;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private Spinner searchBySpinner;
    private View filterLayout;

    private MenuItem searchMenuItem;
    private MenuItem filterMenuItem;
    private MenuItem searchSpinnerItem;
    private MenuItem filterCatalogMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        setupPermissionController();

        presenter = new TaskManagerPresenterImpl(this);

        initView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        FiltersAdapter adapter = new SavedFiltersAdapter(this, builder -> {
            presenter.applyFilter(builder);
            swapFilterLayout();
        });

        filterLayoutWrapper =
                new FilterLayoutWrapper(filterLayout)
                    .setFiltersAdapter(adapter)
                    .onApplyButtonClick(builder -> {
                        presenter.applyFilter(builder);
                        swapFilterLayout();
                    })
                    .onSaveButtonClick(builder ->
                    {
                        EditText filterNameEditText = new EditText(TaskManagerActivity.this);
                        new BottomDialog.Builder(TaskManagerActivity.this)
                                .setTitle("Введите название фильтра")
                                .setCustomView(filterNameEditText)
                                .setNegativeText("Отмена")
                                .setPositiveText("Сохранить")
                                .onPositive(bottomDialog -> {
                                    FiltersStorage.getStorage().putBuilder(
                                            filterNameEditText.getText().toString(),
                                            builder
                                    );

                                    adapter.update();

                                    Toast.makeText(TaskManagerActivity.this,
                                            "Фильтр сохранен", Toast.LENGTH_SHORT).show();
                                }).show();
                    });
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        findViewById(R.id.fab).setOnClickListener(this);

        filterLayout = findViewById(R.id.filter_layout);
    }

    private void setupPermissionController() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .withListener(new PermissionsAdapter() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        super.onPermissionsChecked(report);
                        if (!report.areAllPermissionsGranted()) {
                            Toast.makeText(TaskManagerActivity.this,
                                    "Предоставьте разрешения на работу с External Storage",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .check();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.add(presenter.bindView(new TaskListActive()), getString(R.string.active_tasks_title));
        adapter.add(presenter.bindView(new TaskListCompleted()), getString(R.string.completed_tasks_title));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    private void startToolbarMode(ToolbarMode startedToolbarMode) {
        switch (startedToolbarMode) {
            case FILTER:
            {
                searchMenuItem.setVisible(false);
                searchSpinnerItem.setVisible(false);
                filterCatalogMenuItem.setVisible(true);

                viewPager.setVisibility(View.INVISIBLE);
                tabLayout.setVisibility(View.INVISIBLE);
                filterLayout.setVisibility(View.VISIBLE);

                toolbar.setTitle("Параметры");
                filterMenuItem.setIcon(R.drawable.ic_undo);
            } break;
            case SEARCH:
            {
                searchMenuItem.setVisible(false);
                filterMenuItem.setVisible(false);
                searchSpinnerItem.setVisible(true);
            } break;
            default:
            {
                searchMenuItem.setVisible(true);
                filterMenuItem.setVisible(true);
                searchSpinnerItem.setVisible(false);
                filterCatalogMenuItem.setVisible(false);

                viewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                filterLayout.setVisibility(View.GONE);

                filterMenuItem.setIcon(R.drawable.ic_sort);
                toolbar.setTitle(getString(R.string.app_name));
            } break;
        }
    }

    private void swapFilterLayout() {
        if (filterLayout.getVisibility() == View.GONE) {
            startToolbarMode(ToolbarMode.FILTER);
        } else {
            startToolbarMode(ToolbarMode.NORMAL);
        }
    }

    private void startDirectoryChooser() {
        new DirectoryChooser(this,
                dir ->
                {
                    String json = JSONFactory.toJson(TasksDatabase.getInstance().getAllEntries(), List.class);
                    File itemList = new File(dir, EXPORTED_FILE_NAME);
                    try {
                        FileOutputStream fos = new FileOutputStream(itemList);
                        fos.write(json.getBytes());
                        fos.close();
                        Snackbar.make(getWindow().getDecorView(),
                                "Ваши задачи успешно экспортированы в " + itemList.getAbsolutePath(), 2000)
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Snackbar.make(getWindow().getDecorView(), "Не удалось сохранить данные", 2000).show();
                    }
                }, null);
    }

    private void sendImportRequest() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_IMPORT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_list, menu);

        filterCatalogMenuItem = menu.findItem(R.id.action_saved_filters);
        searchSpinnerItem = menu.findItem(R.id.search_spinner);
        searchBySpinner = (Spinner) searchSpinnerItem.getActionView();
        searchMenuItem = menu.findItem(R.id.action_search);
        filterMenuItem = menu.findItem(R.id.action_filter);

        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setQueryHint("Введите название");

        AutoCompleteTextView searchTextView = (AutoCompleteTextView)
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.white_cursor);
        } catch (Exception ignored) {}

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);
        startToolbarMode(ToolbarMode.NORMAL);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                swapFilterLayout();
                break;
            case R.id.action_export:
                startDirectoryChooser();
                break;
            case R.id.action_import:
                sendImportRequest();
                break;
            case R.id.action_saved_filters:
                filterLayoutWrapper.swapFilterList();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterLayoutWrapper.onCompileFilter(builder -> {
            if (!newText.isEmpty()) {
                switch (searchBySpinner.getSelectedItemPosition()) {
                    case 0:
                        builder.startsWith(TasksDatabaseHelper.TITLE, newText);
                        break;
                    case 1:
                        builder.startsWith(TasksDatabaseHelper.DESCRIPTION, newText);
                        break;
                }
            }

            presenter.applyFilter(builder);
        });

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        startToolbarMode(ToolbarMode.SEARCH);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        startToolbarMode(ToolbarMode.NORMAL);
        return true;
    }


    @Override
    public void startEditor(int id) {
        Intent intent = new Intent(this, TaskEditorActivity.class);
        intent.setAction(ACTION_EDIT);
        intent.putExtra(TasksDatabaseHelper.ID, id);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, TaskEditorActivity.class);
        intent.setAction(ACTION_CREATE);
        startActivityForResult(intent, REQUEST_CREATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onResult(requestCode, resultCode, data);
    }

    @Override
    public void showAlert(String message) {
        Snackbar.make(getWindow().getDecorView(), message, 2000).show();
    }
}
