package ru.urfu.taskmanager.task_manager.main.filter;

import android.support.annotation.NonNull;
import android.view.View;

import ru.urfu.taskmanager.task_manager.main.adapters.FiltersAdapter;
import ru.urfu.taskmanager.utils.db.TasksFilter;

public class FilterLayoutWrapper {
    private final FilterViewHolder holder;

    public FilterLayoutWrapper(@NonNull View filterLayout) {
        this.holder = new FilterViewHolder(filterLayout);
    }

    public FilterLayoutWrapper setFiltersAdapter(FiltersAdapter adapter) {
        holder.savedFiltersList.setAdapter(adapter);
        holder.savedFiltersList.setOnItemClickListener(adapter);
        return this;
    }

    public FilterLayoutWrapper onApplyButtonClick(OnFilterAction onFilterAction) {
        holder.filterApplyButton.setOnClickListener(v -> onCompileFilter(onFilterAction));
        return this;
    }

    public FilterLayoutWrapper onSaveButtonClick(OnFilterAction onFilterAction) {
        holder.filterSaveButton.setOnClickListener(v -> onCompileFilter(onFilterAction));
        return this;
    }

    public void onCompileFilter(OnFilterAction onFilterAction) {
        onFilterAction.onAction(holder.compileFilterBuilder());
    }

    public void swapFilterList() {
        if (holder.savedFiltersList.getVisibility() == View.GONE) {
            holder.savedFiltersList.setVisibility(View.VISIBLE);
        } else {
            holder.savedFiltersList.setVisibility(View.GONE);
        }
    }


    public interface OnFilterAction {
        void onAction(TasksFilter.Builder builder);
    }
}