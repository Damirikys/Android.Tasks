package ru.urfu.taskmanager.utils.tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.urfu.taskmanager.R;

public class DirectoryChooser implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener {
    private List<File> entries = new ArrayList<>();
    private File currentDir;
    private Context context;
    private ListView listView;
    private OnDirectoryChosen listener;

    private class DirAdapter extends ArrayAdapter<File> {
        DirAdapter(@LayoutRes int resid) {
            super(context, resid, entries);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView textview = (TextView) super.getView(position, convertView, parent);

            if (entries.get(position) == null) {
                textview.setText(". . .");
            } else {
                textview.setText(entries.get(position).getName());
            }

            return textview;
        }
    }

    private void listDirs() {
        entries.clear();

        File[] files = currentDir.listFiles();

        if (currentDir.getParent() != null)
            entries.add(new File(". . ."));

        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory())
                    continue;
                entries.add(file);
            }
        }

        Collections.sort(entries, (f1, f2) ->
                f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase())
        );
    }

    public DirectoryChooser(Context ctx, OnDirectoryChosen res, String startDir) {
        context = ctx;
        listener = res;

        if (startDir != null)
            currentDir = new File(startDir);
        else
            currentDir = Environment.getExternalStorageDirectory();

        listDirs();
        DirAdapter adapter = new DirAdapter(R.layout.listitem_row_textview);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(context.getString(R.string.select_directory));
        builder.setAdapter(adapter, this);

        builder.setPositiveButton(context.getString(R.string.select), (dialog, id) -> {
            if (listener != null)
                listener.onChooseDirectory(currentDir.getAbsolutePath());
            dialog.dismiss();
        });

        builder.setNegativeButton(context.getString(R.string.cancel), (dialog, id) -> dialog.cancel());

        AlertDialog m_alertDialog = builder.create();
        listView = m_alertDialog.getListView();
        listView.setOnItemClickListener(this);
        m_alertDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View list, int pos, long id) {
        if (pos < 0 || pos >= entries.size())
            return;

        if (entries.get(pos).getName().equals(". . ."))
            currentDir = currentDir.getParentFile();
        else
            currentDir = entries.get(pos);

        listDirs();
        DirAdapter adapter = new DirAdapter(R.layout.listitem_row_textview);
        listView.setAdapter(adapter);
    }

    public void onClick(DialogInterface dialog, int which) {
    }

    public interface OnDirectoryChosen {
        void onChooseDirectory(String dir);
    }
}