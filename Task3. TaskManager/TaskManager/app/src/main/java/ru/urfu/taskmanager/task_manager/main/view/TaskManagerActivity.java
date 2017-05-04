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
import ru.urfu.taskmanager.utils.db.TasksDatabase;
import ru.urfu.taskmanager.utils.db.TasksDatabaseHelper;
import ru.urfu.taskmanager.utils.db.TasksFilter;
import ru.urfu.taskmanager.utils.tools.DirectoryChooser;
import ru.urfu.taskmanager.utils.tools.JSONFactory;

public class TaskManagerActivity extends AppCompatActivity
        implements TaskManager, MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener
{
    public static final String ACTION_CREATE = "ru.urfu.taskmanager.ACTION_CREATE";
    public static final String ACTION_EDIT = "ru.urfu.taskmanager.ACTION_EDIT";

    public static final int REQUEST_CREATE = 1;
    public static final int REQUEST_EDIT = 2;
    public static final int REQUEST_IMPORT = 3;

    public static final int SNACKBAR_SHOW_TIME = 2000;

    public static final String EXPORTED_FILE_NAME = "itemlist.ili";

    private TaskManagerPresenter mPresenter;

    private enum ToolbarMode {
        NORMAL, SEARCH, FILTER
    }

    private FilterLayoutWrapper mFilterLayoutWrapper;
    private FiltersAdapter mAdapter;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private Spinner mSearchBySpinner;
    private View mFilterLayout;

    private MenuItem mSearchMenuItem;
    private MenuItem mFilterMenuItem;
    private MenuItem mSearchSpinnerItem;
    private MenuItem mFilterCatalogMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        setupPermissionController();

        mPresenter = new TaskManagerPresenterImpl(this);

        initView();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mAdapter = new SavedFiltersAdapter(this, this::onApplyFilter);

        mFilterLayoutWrapper = new FilterLayoutWrapper(mFilterLayout)
                .setFiltersAdapter(mAdapter)
                .onSaveButtonClick(this::onSaveFilter)
                .onApplyButtonClick(this::onApplyFilter);
    }


    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);

        findViewById(R.id.fab).setOnClickListener(this);

        mFilterLayout = findViewById(R.id.filter_layout);
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
                                    getString(R.string.need_permission_external),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .check();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.add(mPresenter.bindView(new TaskListActive()), getString(R.string.active_tasks_title));
        adapter.add(mPresenter.bindView(new TaskListCompleted()), getString(R.string.completed_tasks_title));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    private void startToolbarMode(ToolbarMode startedToolbarMode) {
        switch (startedToolbarMode) {
            case FILTER:
            {
                mSearchMenuItem.setVisible(false);
                mSearchSpinnerItem.setVisible(false);
                mFilterCatalogMenuItem.setVisible(true);

                mViewPager.setVisibility(View.INVISIBLE);
                mTabLayout.setVisibility(View.INVISIBLE);
                mFilterLayout.setVisibility(View.VISIBLE);

                mToolbar.setTitle(getString(R.string.toolbar_filter_title));
                mFilterMenuItem.setIcon(R.drawable.ic_undo);
            }
            break;
            case SEARCH:
            {
                mSearchMenuItem.setVisible(false);
                mFilterMenuItem.setVisible(false);
                mSearchSpinnerItem.setVisible(true);
            }
            break;
            default:
            {
                mSearchMenuItem.setVisible(true);
                mFilterMenuItem.setVisible(true);
                mSearchSpinnerItem.setVisible(false);
                mFilterCatalogMenuItem.setVisible(false);

                mViewPager.setVisibility(View.VISIBLE);
                mTabLayout.setVisibility(View.VISIBLE);
                mFilterLayout.setVisibility(View.GONE);

                mFilterMenuItem.setIcon(R.drawable.ic_sort);
                mToolbar.setTitle(getString(R.string.app_name));
            }
            break;
        }
    }

    private void swapFilterLayout() {
        if (mFilterLayout.getVisibility() == View.GONE) {
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
                                getString(R.string.task_successful_export) + " " + itemList.getAbsolutePath(), SNACKBAR_SHOW_TIME)
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Snackbar.make(getWindow().getDecorView(), getString(R.string.task_export_failed), SNACKBAR_SHOW_TIME).show();
                    }
                }, null);
    }

    private void sendImportRequest() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_IMPORT);
    }

    private void onSaveFilter(TasksFilter.Builder builder) {
        EditText filterNameEditText = new EditText(this);
        new BottomDialog.Builder(this)
                .setTitle(getString(R.string.entry_filter_name))
                .setCustomView(filterNameEditText)
                .setNegativeText(getString(R.string.cancel))
                .setPositiveText(getString(R.string.save))
                .onPositive(bottomDialog ->
                {
                    String name = filterNameEditText.getText().toString();
                    mAdapter.addItem(name, builder);

                    Toast.makeText(TaskManagerActivity.this,
                            getString(R.string.filter_was_saved), Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void onApplyFilter(TasksFilter.Builder builder) {
        mPresenter.applyFilter(builder);
        swapFilterLayout();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_list, menu);

        mFilterCatalogMenuItem = menu.findItem(R.id.action_saved_filters);
        mSearchSpinnerItem = menu.findItem(R.id.search_spinner);
        mSearchBySpinner = (Spinner) mSearchSpinnerItem.getActionView();
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mFilterMenuItem = menu.findItem(R.id.action_filter);

        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setQueryHint(getString(R.string.search_hint));

        AutoCompleteTextView searchTextView = (AutoCompleteTextView)
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.white_cursor);
        } catch (Exception ignored) {
        }

        MenuItemCompat.setOnActionExpandListener(mSearchMenuItem, this);
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
                mFilterLayoutWrapper.swapFilterList();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mFilterLayoutWrapper.onCompileFilter(builder -> {
            if (!newText.isEmpty()) {
                switch (mSearchBySpinner.getSelectedItemPosition()) {
                    case 0:
                        builder.startsWith(TasksDatabaseHelper.TITLE, newText);
                        break;
                    case 1:
                        builder.startsWith(TasksDatabaseHelper.DESCRIPTION, newText);
                        break;
                }
            }

            mPresenter.applyFilter(builder);
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
        mPresenter.onResult(requestCode, resultCode, data);
    }

    @Override
    public void showAlert(String message) {
        Snackbar.make(getWindow().getDecorView(), message, SNACKBAR_SHOW_TIME).show();
    }
}
