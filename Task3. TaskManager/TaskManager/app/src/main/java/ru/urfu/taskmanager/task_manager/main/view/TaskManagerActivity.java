package ru.urfu.taskmanager.task_manager.main.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.lang.reflect.Field;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.auth.models.User;
import ru.urfu.taskmanager.data.db.DbTasksFilter;
import ru.urfu.taskmanager.data.db.DbTasksHelper;
import ru.urfu.taskmanager.data.network.sync_module.BroadcastSyncManager;
import ru.urfu.taskmanager.task_manager.editor.view.EditorPagerFragment;
import ru.urfu.taskmanager.task_manager.editor.view.TaskEditorFragment;
import ru.urfu.taskmanager.task_manager.main.fragments.TaskPagerFragment;
import ru.urfu.taskmanager.task_manager.main.fragments.helper.CursorProvider;
import ru.urfu.taskmanager.task_manager.main.fragments.adapters.TasksListAdapter;
import ru.urfu.taskmanager.task_manager.main.fragments.view.CustomTransition;
import ru.urfu.taskmanager.task_manager.main.adapters.FiltersAdapter;
import ru.urfu.taskmanager.task_manager.main.adapters.PermissionsAdapter;
import ru.urfu.taskmanager.task_manager.main.adapters.SavedFiltersAdapter;
import ru.urfu.taskmanager.task_manager.main.filter.FilterLayoutWrapper;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenter;
import ru.urfu.taskmanager.task_manager.main.presenter.TaskManagerPresenterImpl;
import ru.urfu.taskmanager.utils.tools.DirectoryChooser;

import static ru.urfu.taskmanager.task_manager.editor.view.EditorPagerFragment.EDITOR_PAGER_POSITION;

@SuppressLint("Registered")
@EActivity(R.layout.activity_task_list)
public class TaskManagerActivity extends AppCompatActivity
        implements TaskManager,
        MenuItemCompat.OnActionExpandListener,
        SearchView.OnQueryTextListener,
        NavigationView.OnNavigationItemSelectedListener
{
    public static final int REQUEST_CREATE = 1;
    public static final int REQUEST_EDIT = 2;
    private static final int REQUEST_IMPORT = 3;

    private TaskManagerPresenter mPresenter;

    private enum ToolbarMode {
        NORMAL, SEARCH, FILTER
    }

    private BroadcastReceiver mBroadcastSyncManager;
    private FilterLayoutWrapper mFilterLayoutWrapper;
    private FiltersAdapter mAdapter;

    @ViewById(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @ViewById(R.id.nav_view)
    NavigationView mNavigationView;

    @ViewById(R.id.fragment_place)
    FrameLayout mFragmentLayout;

    @ViewById(R.id.nav_landscape_container)
    FrameLayout mNavigationContainer;

    @ViewById(R.id.animate_progress_bar)
    AnimateHorizontalProgressBar mProgressBar;

    @ViewById(R.id.circleProgressBar)
    ProgressBar mCircleProgressBar;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @ViewById(R.id.filter_layout)
    View mFilterLayout;

    private ProgressDialog mProgressDialog;
    private Spinner mSearchBySpinner;

    private MenuItem mSearchMenuItem;
    private MenuItem mFilterMenuItem;
    private MenuItem mSearchSpinnerItem;
    private MenuItem mFilterCatalogMenuItem;

    @AfterViews
    public void initialize() {
        onConfigurationChanged(getResources().getConfiguration());

        mPresenter = new TaskManagerPresenterImpl(this);

        setupPermissionController();
        setupReceiver();

        mAdapter = new SavedFiltersAdapter(this, this::onApplyFilter);
        mFilterLayoutWrapper = new FilterLayoutWrapper(mFilterLayout)
                .setFiltersAdapter(mAdapter)
                .onSaveButtonClick(this::onSaveFilter)
                .onApplyButtonClick(this::onApplyFilter);


        setSupportActionBar(mToolbar);
        mProgressDialog = new ProgressDialog(this);
        mNavigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_place, TaskPagerFragment.newInstance(new Bundle()))
                .commit();

        //showAlert("USER_ID: " + User.getActiveUser().getUserId());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mNavigationContainer.removeAllViews();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            NavigationView navigationView = new NavigationView(this);
            navigationView.inflateMenu(R.menu.nav_menu_bar);
            navigationView.setNavigationItemSelectedListener(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                navigationView.setElevation(1f);
            }

            mNavigationContainer.addView(navigationView);
        }
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

    private void startToolbarMode(ToolbarMode startedToolbarMode) {
        switch (startedToolbarMode) {
            case FILTER:
            {
                mSearchMenuItem.setVisible(false);
                mSearchSpinnerItem.setVisible(false);
                mFilterCatalogMenuItem.setVisible(true);

                mFragmentLayout.setVisibility(View.INVISIBLE);
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

                mFragmentLayout.setVisibility(View.VISIBLE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMPORT && resultCode == RESULT_OK) {
            mPresenter.importData(data.getData());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                swapFilterLayout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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
                syncData();
                break;
        }

        return false;
    }

    @Override
    public void syncData() {
        sendBroadcast(new Intent(BroadcastSyncManager.SYNC_START_ACTION));
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
    public TaskManagerPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public FilterLayoutWrapper getFilterLayoutWrapper() {
        return mFilterLayoutWrapper;
    }

    @Override
    public void startEditor(@Nullable Integer position, CursorProvider adapter, TasksListAdapter.ViewHolder holder) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EDITOR_PAGER_POSITION, position);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment editorFragment;

        if (position == null) {
            editorFragment = TaskEditorFragment.newInstance(bundle);
        } else {
            editorFragment = EditorPagerFragment.newInstance(bundle)
                    .bindCursorProvider(adapter);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && holder != null) {
                editorFragment.setSharedElementEnterTransition(new CustomTransition());
                editorFragment.setSharedElementReturnTransition(new CustomTransition());
                editorFragment.setEnterTransition(new Fade(Fade.IN));
                editorFragment.setExitTransition(new Fade(Fade.OUT));

                getSupportFragmentManager().getFragments().get(0)
                        .setEnterTransition(new Fade(Fade.IN));
                getSupportFragmentManager().getFragments().get(0)
                        .setExitTransition(new Fade(Fade.OUT));

                transaction.addSharedElement(holder.ttl, "timeBlock_" + position);
            }
        }

        transaction.replace(R.id.fragment_place, editorFragment)
            .addToBackStack(null)
            .commit();
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
        mFragmentLayout.setVisibility(View.INVISIBLE);
        mCircleProgressBar.setVisibility(View.VISIBLE);
    }

    @UiThread
    public void hideProgress() {
        mCircleProgressBar.setVisibility(View.GONE);
        mFragmentLayout.setVisibility(View.VISIBLE);
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    @UiThread
    public void showAlert(String message) {
        Snackbar.make(getWindow().getDecorView(), message, TIMEOUT_IN_MILLIS).show();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        unregisterReceiver(mBroadcastSyncManager);
        super.onDestroy();
    }
}
