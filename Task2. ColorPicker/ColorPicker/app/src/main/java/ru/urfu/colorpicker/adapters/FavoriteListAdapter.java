package ru.urfu.colorpicker.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ru.urfu.colorpicker.Application;
import ru.urfu.colorpicker.R;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.ViewHolder>
{
    private EventListener eventListener;
    private List<Integer> items;

    public FavoriteListAdapter(EventListener eventListener, List<Integer> items) {
        this.eventListener = eventListener;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int color = items.get(position);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        holder.represent.setBackgroundColor(color);
        holder.value.setText(String.format("#%06X", 0xFFFFFF & color));
        holder.setOnSwipeListener(new OnSwipeListener(holder));
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
        GestureDetector detector;
        LinearLayout layout;
        ImageView represent;
        TextView value;

        ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.favorite_list_item);
            represent = (ImageView) itemView.findViewById(R.id.favorite_color_image);
            value = (TextView) itemView.findViewById(R.id.favorite_color_value);
        }

        void setOnSwipeListener(OnSwipeListener onSwipeListener) {
            detector = new GestureDetector(Application.getContext(), onSwipeListener);
            itemView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        onSwipeListener.onUp(event);
                }

                return detector.onTouchEvent(event);
            });
        }
    }

    private class OnSwipeListener extends SimpleOnGestureListener
    {
        View view;
        int paddingStart, paddingTop, paddingEnd, paddingBottom;
        int position;

        int downX;

        boolean toDelete = false;

        OnSwipeListener(ViewHolder holder) {
            this.view = holder.layout;
            this.paddingStart = view.getPaddingLeft();
            this.paddingBottom = view.getPaddingBottom();
            this.paddingTop = view.getPaddingTop();
            this.paddingEnd = view.getPaddingRight();
            this.position = holder.getAdapterPosition();
        }

        @Override
        public boolean onDown(MotionEvent e) {
            downX = (int) e.getX();
            return true;
        }

        void onUp(MotionEvent e) {
            view.setBackgroundColor(Color.TRANSPARENT);
            if (toDelete) removeItem();
            view.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
            eventListener.onScrollingEnable();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            eventListener.onColorSelected(items.get(position));
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            eventListener.onScrollingDisable();
            view.setPadding((int) e2.getX() - downX, paddingTop, paddingEnd, paddingBottom);
            toDelete = view.getWidth() - (int) e2.getX() <= view.getWidth() / 3;
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        private void removeItem() {
            eventListener.onColorDeleted(items.get(position));
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    public interface EventListener {
        void onScrollingEnable();
        void onScrollingDisable();
        void onColorSelected(int color);
        void onColorDeleted(int color);
    }
}
