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
import ru.urfu.taskmanager.task_manager.main.view.TaskManagerActivity;

public class DirectoryChooser implements AdapterView.OnItemClickListener, DialogInterface.OnClickListener
{
    private static final String BACKWARD = ". . .";
    private Context mContext;

    private List<File> mEntries = new ArrayList<>();
    private File mCurrentDir;
    private ListView mListView;
    private OnDirectoryChosen mListener;

    public DirectoryChooser(Context ctx, OnDirectoryChosen res, String startDir) {
        mContext = ctx;
        mListener = res;

        if (startDir != null) mCurrentDir = new File(startDir);
        else mCurrentDir = Environment.getExternalStorageDirectory();

        listDirs();
        DirAdapter adapter = new DirAdapter(R.layout.listitem_row_textview);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(mContext.getString(R.string.select_directory));
        builder.setAdapter(adapter, this);

        builder.setPositiveButton(mContext.getString(R.string.select), (dialog, id) -> {
            if (mListener != null) mListener.onChooseDirectory(mCurrentDir.getAbsolutePath());
            dialog.dismiss();
        });

        builder.setNegativeButton(mContext.getString(R.string.cancel), (dialog, id) -> dialog.cancel());

        AlertDialog m_alertDialog = builder.create();
        mListView = m_alertDialog.getListView();
        mListView.setOnItemClickListener(this);
        m_alertDialog.show();
    }

    private void listDirs() {
        mEntries.clear();

        File[] files = mCurrentDir.listFiles();

        if (mCurrentDir.getParent() != null) mEntries.add(new File(BACKWARD));

        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) continue;
                mEntries.add(file);
            }
        }

        Collections.sort(mEntries, (f1, f2) -> f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase()));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View list, int pos, long id) {
        if (pos < 0 || pos >= mEntries.size()) return;

        if (mEntries.get(pos).getName().equals(BACKWARD)) mCurrentDir = mCurrentDir.getParentFile();
        else mCurrentDir = mEntries.get(pos);

        listDirs();
        DirAdapter adapter = new DirAdapter(R.layout.listitem_row_textview);
        mListView.setAdapter(adapter);
    }

    public void onClick(DialogInterface dialog, int which) {
    }

    public interface OnDirectoryChosen
    {
        void onChooseDirectory(String dir);
    }

    private class DirAdapter extends ArrayAdapter<File>
    {
        DirAdapter(@LayoutRes int resid) {
            super(mContext, resid, mEntries);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView textview = (TextView) super.getView(position, convertView, parent);

            if (mEntries.get(position) == null) {
                textview.setText(BACKWARD);
            } else {
                textview.setText(mEntries.get(position).getName());
            }

            return textview;
        }
    }
}