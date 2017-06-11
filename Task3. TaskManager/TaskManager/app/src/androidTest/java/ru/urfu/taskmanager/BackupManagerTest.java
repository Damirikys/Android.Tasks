package ru.urfu.taskmanager;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class BackupManagerTest extends TaskManagerInstrumentedTest
{
    @Test
    public void exportTest() {
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(mActivityRule.getActivity().getString(R.string.export_title)))
                .perform(click());
        onView(withText(mActivityRule.getActivity().getString(R.string.select)))
                .perform(click());

        checkFinishExportProcess();
    }

    private void checkFinishExportProcess() {
        try {
            onView(withText(R.string.task_successful_export))
                    .check(matches(withEffectiveVisibility(
                            ViewMatchers.Visibility.VISIBLE
                    )));
        } catch (NoMatchingViewException e) {
            try {
                onView(withText(R.string.task_export_failed))
                        .check(matches(withEffectiveVisibility(
                                ViewMatchers.Visibility.VISIBLE
                        )));

                throw new RuntimeException("Test failed");
            } catch (NoMatchingViewException e1) {
                waitView();
                checkFinishExportProcess();
            }
        }
    }

    @Test
    public void importTest() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withText(mActivityRule.getActivity().getString(R.string.import_title)))
                .perform(click());

        waitView();

        try {
            device.findObject(By.scrollable(true))
                    .scroll(Direction.DOWN, 100f);
            device.findObject(By.textContains("itemlist.ili"))
                    .click();

            waitView();
            checkFinishImportProcess();
        } catch (NullPointerException e) {
            // Not default file manager
            device.pressBack();
            device.pressBack();
        }
    }

    private void checkFinishImportProcess() {
        try {
            onView(withText(R.string.task_successful_import))
                    .check(matches(withEffectiveVisibility(
                            ViewMatchers.Visibility.VISIBLE
                    )));
        } catch (NoMatchingViewException e) {
            try {
                onView(withText(R.string.task_import_failed))
                        .check(matches(withEffectiveVisibility(
                                ViewMatchers.Visibility.VISIBLE
                        )));

                throw new RuntimeException("Test failed");
            } catch (NoMatchingViewException e1) {
                waitView();
                checkFinishExportProcess();
            }
        }
    }
}
