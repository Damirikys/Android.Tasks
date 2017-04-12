package ru.urfu.colorpicker.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.urfu.colorpicker.R;


public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder>
{
    private List<Integer> items;

    public FavoriteListAdapter(List<Integer> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int color = items.get(items.size() - position - 1);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        holder.represent.setBackgroundColor(color);
        holder.value.setText(String.format("#%06X", 0xFFFFFF & color));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(int color) {
        items.add(color);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView represent;
        TextView value;

        ViewHolder(View itemView) {
            super(itemView);
            represent = (ImageView) itemView.findViewById(R.id.favorite_color_image);
            value = (TextView) itemView.findViewById(R.id.favorite_color_value);
        }
    }
}
