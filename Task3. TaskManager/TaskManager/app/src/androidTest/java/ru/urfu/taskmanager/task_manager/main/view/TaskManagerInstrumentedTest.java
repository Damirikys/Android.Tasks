package ru.urfu.taskmanager.task_manager.main.view;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;

import ru.urfu.taskmanager.auth.models.User;

abstract class TaskManagerInstrumentedTest
{
    protected static final long TIMEOUT = 1000;
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
}
