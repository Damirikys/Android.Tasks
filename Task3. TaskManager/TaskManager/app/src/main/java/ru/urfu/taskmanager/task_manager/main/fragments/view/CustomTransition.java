package ru.urfu.taskmanager.task_manager.main.fragments.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CustomTransition extends android.transition.TransitionSet
{
    public CustomTransition() {
        init();
    }

    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new android.transition.ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}