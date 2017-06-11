package ru.urfu.taskmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.urfu.taskmanager.sync.BroadcastSyncManager;
import ru.urfu.taskmanager.entities.TaskEntry;
import ru.urfu.taskmanager.entities.TaskEntryCouples;

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
import static ru.urfu.taskmanager.sync.SynchronizeService.EXTRA_KEY;

@RunWith(AndroidJUnit4.class)
public class SynchronizeServiceTest extends TaskManagerInstrumentedTest
{
    @Test
    public void forceSync() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(mActivityRule.getActivity().getString(R.string.sync_title)))
                .perform(click());

        checkFinishSuccessSyncProcess();
    }

    @Test
    public void syncAskAction() {
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

        waitView();

        onView(withId(R.id.second_container))
                .perform(click());

        checkFinishSuccessSyncProcess();
    }

    @Test
    public void sendOtherSyncActions() {
        mActivityRule.getActivity().sendBroadcast(
                new Intent(BroadcastSyncManager.SYNC_SCHEDULE_ACTION)
        );

        mActivityRule.getActivity().sendBroadcast(
                new Intent(BroadcastSyncManager.SYNC_SUCCESS_ACTION)
        );
    }

    private void checkFinishSuccessSyncProcess() {
        waitView();

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
                checkFinishSuccessSyncProcess();
            }
        }
    }
}
