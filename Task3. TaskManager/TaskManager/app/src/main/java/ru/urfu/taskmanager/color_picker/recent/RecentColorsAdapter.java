package ru.urfu.taskmanager.color_picker.recent;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.utils.interfaces.Callback;

class RecentColorsAdapter extends RecyclerView.Adapter<RecentColorsAdapter.ViewHolder> {
    private List<Integer> data;
    private Callback<Integer> onItemClick;

    RecentColorsAdapter(Callback<Integer> onItemClick) {
        this.data = RecentColorsStorage.getRepository().getItems();
        this.onItemClick = onItemClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_colors_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.colorView.setBackgroundColor(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View colorView;

        ViewHolder(View view) {
            super(view);
            this.colorView = view.findViewById(R.id.color_view);
            this.colorView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            RecentColorsAdapter.this.onItemClick.call(
                    ((ColorDrawable) colorView.getBackground())
                            .getColor());
        }
    }
}
