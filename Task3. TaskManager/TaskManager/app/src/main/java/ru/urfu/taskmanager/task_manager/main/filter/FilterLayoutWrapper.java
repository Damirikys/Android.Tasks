package ru.urfu.taskmanager.task_manager.main.filter;

import android.support.annotation.NonNull;
import android.view.View;

import ru.urfu.taskmanager.task_manager.main.adapters.FiltersAdapter;
import ru.urfu.taskmanager.data.db.DbTasksFilter;

public class FilterLayoutWrapper
{
    private final FilterViewHolder mHolder;

    public FilterLayoutWrapper(@NonNull View filterLayout) {
        this.mHolder = new FilterViewHolder(filterLayout);
    }

    public FilterLayoutWrapper setFiltersAdapter(FiltersAdapter adapter) {
        mHolder.mSavedFiltersList.setAdapter(adapter);
        mHolder.mSavedFiltersList.setOnItemClickListener(adapter);
        return this;
    }

    public FilterLayoutWrapper onApplyButtonClick(OnFilterAction onFilterAction) {
        mHolder.mFilterApplyButton.setOnClickListener(v -> onCompileFilter(onFilterAction));
        return this;
    }

    public FilterLayoutWrapper onSaveButtonClick(OnFilterAction onFilterAction) {
        mHolder.mFilterSaveButton.setOnClickListener(v -> onCompileFilter(onFilterAction));
        return this;
    }

    public void onCompileFilter(OnFilterAction onFilterAction) {
        onFilterAction.onAction(mHolder.compileFilterBuilder());
    }

    public void swapFilterList() {
        if (mHolder.mSavedFiltersList.getVisibility() == View.GONE) {
            mHolder.mSavedFiltersList.setVisibility(View.VISIBLE);
        } else {
            mHolder.mSavedFiltersList.setVisibility(View.GONE);
        }
    }


    public interface OnFilterAction {
        void onAction(DbTasksFilter.Builder builder);
    }
}