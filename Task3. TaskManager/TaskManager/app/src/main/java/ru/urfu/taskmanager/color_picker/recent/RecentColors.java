package ru.urfu.taskmanager.color_picker.recent;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.github.javiersantos.bottomdialogs.BottomDialog;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.utils.interfaces.Callback;

public final class RecentColors
{
    public static void showRecent(Context context, Callback<Integer> selectedColor) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.recent_colors, null);

        RecyclerView recyclerView = (RecyclerView) customView.findViewById(R.id.recent_colors_recycler);
        recyclerView.setAdapter(new RecentColorsAdapter(selectedColor));
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        new BottomDialog.Builder(context)
                .setTitle(context.getString(R.string.recent_colors))
                .setCustomView(customView)
                .show();
    }
}
