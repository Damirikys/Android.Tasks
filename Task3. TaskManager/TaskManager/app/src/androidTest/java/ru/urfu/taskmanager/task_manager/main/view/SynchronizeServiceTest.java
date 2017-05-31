package ru.urfu.taskmanager.task_manager.main.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.urfu.taskmanager.R;
import ru.urfu.taskmanager.data.network.sync_module.BroadcastSyncManager;
import ru.urfu.taskmanager.task_manager.models.TaskEntry;
import ru.urfu.taskmanager.task_manager.models.TaskEntryCouples;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static ru.urfu.taskmanager.data.network.sync_module.SynchronizeService.EXTRA_KEY;

@RunWith(AndroidJUnit4.class)
public class SynchronizeServiceTest extends TaskManagerInstrumentedTest
{
    @Test
    public void forceSyncTest() {
        // Open nav
        onView(withId(R.id.drawer_layout)).perform(open());
        // Click to force sync
        onView(withText(mActivityRule.getActivity().getString(R.string.sync_title)))
                .perform(click());

        checkFinishForceSyncProcess();
    }

    private void checkFinishForceSyncProcess() {
        SystemClock.sleep(2000);

        try {
            onView(withText(R.string.sync_success))
                    .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                    .check(matches(isDisplayed()));
        } catch (NoMatchingViewException e) {
            try {
                onView(withText(R.string.sync_failed))
                        .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                        .check(matches(isDisplayed()));

                throw new RuntimeException("Test failed");
            } catch (NoMatchingViewException e1) {
                checkFinishForceSyncProcess();
            }
        }
    }

    @Test
    public void syncAskActionTest() {
        long time = System.currentTimeMillis();

        TaskEntry first = new TaskEntry()
                .setTitle("Title")
                .setDescription("Description")
                .setCreated(time)
                .setEdited(time)
                .setTtl(time)
                .setColor(Color.BLACK);

        TaskEntry second = new TaskEntry()
                .setTitle("Title")
                .setDescription("Description")
                .setCreated(time)
                .setEdited(time)
                .setTtl(time)
                .setColor(Color.BLUE);

        TaskEntryCouples entryCouples = new TaskEntryCouples();
        entryCouples.put(first, second);

        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_KEY, entryCouples);

        Intent intent = new Intent(BroadcastSyncManager.SYNC_ASK_ACTION);
        intent.putExtras(bundle);

        mActivityRule.getActivity().sendBroadcast(intent);

        SystemClock.sleep(2000);

        onView(withId(R.id.second_container))
                .perform(click());

        SystemClock.sleep(2000);
    }
}
