package ru.urfu.taskmanager.task_manager.main.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Field;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.auth.models.User;
import ru.urfu.taskmanager.data.db.DbTasksFilter;
import ru.urfu.taskmanager.data.db.DbTasksHelper;
import ru.urfu.taskmanager.data.network.sync_module.BroadcastSyncManager;
import ru.urfu.taskmanager.task_manager.editor.view.TaskEditorActivity_;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListActive;
import ru.urfu.taskmanager.task_manager.fragments.view.TaskListCompleted;
import ru.urfu.taskmanager.task_manager.main.adapters.FiltersAdapter;
import ru.urfu.taskmanager.task_manager.main.adapters.PermissionsAdapter;
import ru.urfu.taskmanager.task_manager.main.adapters.SavedFiltersAdapter;
import ru.urfu.taskmanager.task_manager.main.adapters.ViewPagerAdapter;
import ru.urfu.taskmanager.task_manager.main.filter.FilterLayoutWrapper;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenter;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenterImpl;
import ru.urfu.taskmanager.utils.tools.DirectoryChooser;

@EActivity(R.layout.activity_task_list)
public class TaskManagerActivity extends AppCompatActivity
        implements TaskManager, MenuItemCompat.OnActionExpandListener, SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener
{
    public static final String ACTION_CREATE = "ru.urfu.taskmanager.ACTION_CREATE";
    public static final String ACTION_EDIT = "ru.urfu.taskmanager.ACTION_EDIT";

    public static final int REQUEST_CREATE = 1;
    public static final int REQUEST_EDIT = 2;
    public static final int REQUEST_IMPORT = 3;

    public static final int SNACKBAR_SHOW_TIME = 2000;

    private TaskManagerPresenter mPresenter;

    private enum ToolbarMode {
        NORMAL, SEARCH, FILTER
    }

    private BroadcastReceiver mBroadcastSyncManager;
    private FilterLayoutWrapper mFilterLayoutWrapper;
    private FiltersAdapter mAdapter;

    @ViewById(R.id.animate_progress_bar)
    AnimateHorizontalProgressBar mProgressBar;

    @ViewById(R.id.circleProgressBar)
    ProgressBar mCircleProgressBar;

    @ViewById(R.id.viewpager)
    ViewPager mViewPager;

    @ViewById(R.id.tablayout)
    TabLayout mTabLayout;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.filter_layout)
    View mFilterLayout;

    @ViewById(R.id.fab)
    FloatingActionButton floatingActionButton;

    ProgressDialog mProgressDialog;
    Spinner mSearchBySpinner;

    MenuItem mSearchMenuItem;
    MenuItem mFilterMenuItem;
    MenuItem mSearchSpinnerItem;
    MenuItem mFilterCatalogMenuItem;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupPermissionController();
        setupReceiver();
        initialize();

        mAdapter = new SavedFiltersAdapter(this, this::onApplyFilter);
        mFilterLayoutWrapper = new FilterLayoutWrapper(mFilterLayout)
                .setFiltersAdapter(mAdapter)
                .onSaveButtonClick(this::onSaveFilter)
                .onApplyButtonClick(this::onApplyFilter);
    }


    private void initialize() {
        mPresenter = new TaskManagerPresenterImpl(this);

        setSupportActionBar(mToolbar);
        mViewPager.setOffscreenPageLimit(2);
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mProgressDialog = new ProgressDialog(this);
        floatingActionButton.setOnClickListener(this);

        showAlert("USER_ID: " + User.getActiveUser().getUserId());
    }

    private void setupReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(BroadcastSyncManager.SYNC_ASK_ACTION);
        filter.addAction(BroadcastSyncManager.SYNC_SUCCESS_ACTION);
        filter.addAction(BroadcastSyncManager.SYNC_FAILED_ACTION);
        filter.addAction(BroadcastSyncManager.SYNC_START_ACTION);
        filter.addAction(BroadcastSyncManager.SYNC_SCHEDULE_ACTION);

        mBroadcastSyncManager = new BroadcastSyncManager(this) {
            @Override
            public void onStopSync() {
                mPresenter.applyFilter(DbTasksFilter.DEFAULT_BUILDER);
            }
        };

        registerReceiver(mBroadcastSyncManager, filter);
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
        new DirectoryChooser(this, dir -> mPresenter.exportData(dir), null);
    }

    private void sendImportRequest() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_IMPORT);
    }

    private void onSaveFilter(DbTasksFilter.Builder builder) {
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

    private void onApplyFilter(DbTasksFilter.Builder builder) {
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
            case R.id.action_generation:
                generateBigData();
                break;
            case R.id.action_sync:
                sendBroadcast(new Intent(BroadcastSyncManager.SYNC_START_ACTION));
        }

        return super.onOptionsItemSelected(item);
    }

    private void generateBigData() {
        mPresenter.generateBigData();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mFilterLayoutWrapper.onCompileFilter(builder -> {
            if (!newText.isEmpty()) {
                switch (mSearchBySpinner.getSelectedItemPosition()) {
                    case 0:
                        builder.match(DbTasksHelper.TITLE, newText);
                        break;
                    case 1:
                        builder.match(DbTasksHelper.DESCRIPTION, newText);
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
        Intent intent = new Intent(this, TaskEditorActivity_.class);
        intent.setAction(ACTION_EDIT);
        intent.putExtra(DbTasksHelper.ID, id);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, TaskEditorActivity_.class);
        intent.setAction(ACTION_CREATE);
        startActivityForResult(intent, REQUEST_CREATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        sendBroadcast(new Intent(BroadcastSyncManager.SYNC_START_ACTION));
    }

    @UiThread
    public void startProgressIndicator(int max) {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setMax(max);
    }

    @UiThread
    public void setProgressIndicatorValue(int value) {
        mProgressBar.setProgressWithAnim(value);
    }

    @UiThread
    public void stopProgressIndicator() {
        mProgressBar.setVisibility(View.GONE);
    }

    @UiThread
    public void showProgress(String title, String message) {
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    @UiThread
    public void showProgress() {
        mViewPager.setVisibility(View.GONE);
        mCircleProgressBar.setVisibility(View.VISIBLE);
    }

    @UiThread
    public void hideProgress() {
        mCircleProgressBar.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    @UiThread
    public void showAlert(String message) {
        Snackbar.make(getWindow().getDecorView(), message, SNACKBAR_SHOW_TIME).show();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        unregisterReceiver(mBroadcastSyncManager);
        super.onDestroy();
    }
}
