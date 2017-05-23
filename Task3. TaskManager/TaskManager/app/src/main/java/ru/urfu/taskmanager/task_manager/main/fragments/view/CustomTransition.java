package ru.urfu.taskmanager.task_manager.main.fragments.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.util.AttributeSet;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CustomTransition extends android.transition.TransitionSet
{
    public CustomTransition() {
        init();
    }

    public CustomTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new android.transition.ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}