package ru.urfu.taskmanager;


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TaskEntryOptionsTest extends TaskManagerInstrumentedTest
{
    @Test
    public void postponeTaskEntry() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());
        waitView();
        UiObject2 taskList = device.findObject(By.res(PACKAGE, "task_list"));
        taskList.findObject(By.res(PACKAGE, "task_layout")).click();
        waitView();
        device.findObject(By.textContains("Отложить задачу")).click();
    }

    @Test
    public void taskIsComplete() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());
        waitView();
        UiObject2 taskList = device.findObject(By.res(PACKAGE, "task_list"));
        taskList.findObject(By.res(PACKAGE, "task_layout")).click();
        waitView();
        device.findObject(By.textContains("Задача выполнена")).click();
    }

    @Test
    public void removeTaskEntry() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());
        waitView();
        UiObject2 taskList = device.findObject(By.res(PACKAGE, "task_list"));
        taskList.findObject(By.res(PACKAGE, "task_layout")).click();
        waitView();
        device.findObject(By.textContains("Удалить")).click();
    }

    @Test
    public void restoreTaskEntry() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());
        waitView();
        onView(withText("Выполнено")).perform(click());

        UiObject2 taskList = device.findObject(By.res(PACKAGE, "task_list"));
        taskList.findObject(By.res(PACKAGE, "task_layout")).click();
        waitView();

        onView(withText("Восстановить задачу")).perform(click());
    }
}
