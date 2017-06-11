package ru.urfu.taskmanager;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TaskFilterTest extends TaskManagerInstrumentedTest
{
    @Test
    public void createCustomFilterSaveAndApply() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        device.findObject(By.res(PACKAGE, "action_filter")).click();
        device.findObject(By.res(PACKAGE, "sort_by_spinner")).click();
        waitView();
        device.findObject(By.textContains("Дате создания")).click();
        waitView();
        device.findObject(By.res(PACKAGE, "switch_date_picker")).click();
        waitView();
        device.findObject(By.textContains(getString(R.string.by_range))).click();
        onView(withId(R.id.switch_color_picker)).perform(click());
        waitView();
        pressBack();
        waitView();
        onView(withId(R.id.alphabetically_switch)).perform(click());
        onView(withText(getString(R.string.descendyngly))).perform(click());
        onView(withText(getString(R.string.apply))).perform(click());
    }
}
