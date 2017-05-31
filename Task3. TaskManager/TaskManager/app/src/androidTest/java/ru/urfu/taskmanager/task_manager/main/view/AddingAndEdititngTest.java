package ru.urfu.taskmanager.task_manager.main.view;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.TypeTextAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import ru.urfu.taskmanager.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AddingAndEdititngTest extends TaskManagerInstrumentedTest
{
    private static final String imageUrl = "https://unch.ru/uploads/posts/2017-04/medium/1493442875_noavatar.jpg";

    @Test
    public void createNewTaskEntryTest() {
        // Open editor
        onView(withId(R.id.fab)).perform(click());

        String title = "";
        for (String titleWord : generateRandomWords(2)) {
            title += titleWord + " ";
        }

        // Type title
        onView(withId(R.id.title_edit_field))
                .perform(new TypeTextAction(title, true));

        String desc = "";
        for (String descWord : generateRandomWords(4)) {
            desc += descWord + " ";
        }

        // Type description
        onView(withId(R.id.descr_edit_field))
                .perform(new TypeTextAction(desc, true));
        // Type image url
        onView(withId(R.id.image_url_edit_field))
                .perform(new TypeTextAction(imageUrl, true));
        // Scroll to save button and click
        onView(withId(R.id.save_button))
                .perform(scrollTo(), click());

        Espresso.closeSoftKeyboard();

        waitView();
        // Check result
        onView(withText(R.string.task_was_created))
                .check(matches(withEffectiveVisibility(
                        ViewMatchers.Visibility.VISIBLE
                )));
    }

    @Test
    public void editTaskEntryTest() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        UiObject2 taskList = device.findObject(By.res("ru.urfu.taskmanager", "task_list"));
        taskList.findObject(By.res("ru.urfu.taskmanager", "task_layout"))
                .click();

        waitView();

        device.findObject(By.textContains("Редактировать"))
                .click();

        waitView();

        UiObject2 object = device.findObject(By.res("ru.urfu.taskmanager", "task_editor_scrollview"));

        String title = "";
        for (String titleWord : generateRandomWords(2)) {
            title += titleWord + " ";
        }

        // Type title
        UiObject2 titleField = object.findObject(By.res("ru.urfu.taskmanager", "title_edit_field"));
        titleField.clear();
        titleField.setText(title);

        String desc = "";
        for (String descWord : generateRandomWords(3)) {
            desc += descWord + " ";
        }

        // Type description
        UiObject2 descField = object.findObject(By.res("ru.urfu.taskmanager", "descr_edit_field"));
        descField.clear();
        descField.setText(desc);
        // Type image url
        UiObject2 imgUrlField = object.findObject(By.res("ru.urfu.taskmanager", "image_url_edit_field"));
        imgUrlField.clear();
        imgUrlField.setText(imageUrl);

        object.scroll(Direction.DOWN, 100f);


        object.findObject(By.res("ru.urfu.taskmanager", "save_button"))
                .click();

        waitView();

        // Check result
        onView(withText(R.string.task_was_updated))
                .check(matches(withEffectiveVisibility(
                        ViewMatchers.Visibility.VISIBLE
                )));
    }

    @Test
    public void postponeTaskEntryTest() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        UiObject2 taskList = device.findObject(By.res("ru.urfu.taskmanager", "task_list"));
        taskList.findObject(By.res("ru.urfu.taskmanager", "task_layout")).click();
        device.findObject(By.textContains("Отложить задачу")).click();
        device.findObject(By.textContains("ОК")).click();
    }

    @Test
    public void taskIsCompleteTest() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        UiObject2 taskList = device.findObject(By.res("ru.urfu.taskmanager", "task_list"));
        taskList.findObject(By.res("ru.urfu.taskmanager", "task_layout")).click();
        device.findObject(By.textContains("Задача выполнена")).click();
    }

    @Test
    public void removeTaskEntryTest() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        UiObject2 taskList = device.findObject(By.res("ru.urfu.taskmanager", "task_list"));
        taskList.findObject(By.res("ru.urfu.taskmanager", "task_layout")).click();
        device.findObject(By.textContains("Удалить")).click();
    }

    @Test
    public void restoreTaskEntryTest() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());
        waitView();
        onView(withText("Выполнено")).perform(click());

        UiObject2 taskList = device.findObject(By.res("ru.urfu.taskmanager", "task_list"));
        taskList.findObject(By.res("ru.urfu.taskmanager", "task_layout")).click();
        waitView();

        onView(withText("Восстановить задачу")).perform(click());
        waitView();

        device.findObject(By.textContains("ОК")).click();
    }

    public static String[] generateRandomWords(int numberOfWords)
    {
        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for(int i = 0; i < numberOfWords; i++)
        {
            char[] word = new char[random.nextInt(5)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
            for(int j = 0; j < word.length; j++)
            {
                word[j] = (char)('a' + random.nextInt(26));
            }
            randomStrings[i] = new String(word);
        }
        return randomStrings;
    }
}
