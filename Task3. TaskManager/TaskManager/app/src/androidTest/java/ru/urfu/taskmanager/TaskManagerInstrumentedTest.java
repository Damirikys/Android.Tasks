package ru.urfu.taskmanager;

import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;

import ru.urfu.taskmanager.entities.User;
import ru.urfu.taskmanager.view.main.view.TaskManagerActivity_;

abstract class TaskManagerInstrumentedTest
{
    protected static final String PACKAGE = "ru.urfu.taskmanager";
    private static final long TIMEOUT = 1000;
    private static final String testUserName = "TestUser";

    @Rule
    public ActivityTestRule<TaskManagerActivity_> mActivityRule = new ActivityTestRule<TaskManagerActivity_>(TaskManagerActivity_.class)
    {
        @Override
        protected void beforeActivityLaunched() {
            User.doLogin(InstrumentationRegistry.getTargetContext(), testUserName);
            super.beforeActivityLaunched();
        }
    };

    public void waitView() {
        SystemClock.sleep(TIMEOUT);
    }

    public String getString(@StringRes int resId) {
        return mActivityRule.getActivity().getString(resId);
    }
}
