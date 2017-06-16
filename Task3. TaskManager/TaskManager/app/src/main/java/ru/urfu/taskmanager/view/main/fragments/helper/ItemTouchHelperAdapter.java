package ru.urfu.taskmanager.view.main.fragments.helper;

@SuppressWarnings("UnusedParameters")
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
