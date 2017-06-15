package ru.urfu.taskmanager;

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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TaskEditorTest extends TaskManagerInstrumentedTest
{
    private static final String IMAGE_URL =
            "https://unch.ru/uploads/posts/2017-04/medium/1493442875_noavatar.jpg";

    @Test
    public void createNewTaskEntry() {
        waitView();

        onView(withId(R.id.fab)).perform(click());

        String title = "";
        for (String titleWord : generateRandomWords(2)) {
            title += titleWord + " ";
        }

        waitView();

        onView(withId(R.id.title_edit_field))
                .perform(new TypeTextAction(title, true));

        String desc = "";
        for (String descWord : generateRandomWords(4)) {
            desc += descWord + " ";
        }

        onView(withId(R.id.descr_edit_field))
                .perform(new TypeTextAction(desc, true));
        onView(withId(R.id.image_url_edit_field))
                .perform(new TypeTextAction(IMAGE_URL, true));
        onView(withId(R.id.save_button))
                .perform(scrollTo(), click());

        Espresso.closeSoftKeyboard();

        waitView();
        onView(withText(R.string.task_was_created))
                .check(matches(withEffectiveVisibility(
                        ViewMatchers.Visibility.VISIBLE
                )));
    }

    @Test
    public void editTaskEntry() {
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        UiObject2 taskList = device.findObject(By.res(PACKAGE, "task_list"));
        waitView();
        taskList.findObject(By.res(PACKAGE, "task_layout"))
                .click();

        waitView();

        device.findObject(By.textContains("Редактировать"))
                .click();

        waitView();

        UiObject2 object = device.findObject(By.res(PACKAGE, "task_editor_scrollview"));

        String title = "";
        for (String titleWord : generateRandomWords(2)) {
            title += titleWord + " ";
        }

        // Print title
        UiObject2 titleField = object.findObject(By.res(PACKAGE, "title_edit_field"));
        titleField.clear();
        titleField.setText(title);

        String desc = "";
        for (String descWord : generateRandomWords(3)) {
            desc += descWord + " ";
        }

        // Print description
        UiObject2 descField = object.findObject(By.res(PACKAGE, "descr_edit_field"));
        descField.clear();
        descField.setText(desc);

        // Print image url
        UiObject2 imgUrlField = object.findObject(By.res(PACKAGE, "image_url_edit_field"));
        imgUrlField.clear();
        imgUrlField.setText(IMAGE_URL);

        object.findObject(By.res(PACKAGE, "image_view"))
                .scroll(Direction.DOWN, 100f);

        object.findObject(By.text("Готово")).click();

        waitView();
    }

    @Test
    public void swipeAction() {
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
        object.swipe(Direction.LEFT, 1.0f);
        waitView();
        object.swipe(Direction.RIGHT, 1.0f);
    }

    @Test
    public void refuseFromCreateIntent() {
        onView(withId(R.id.fab)).perform(click());
        Espresso.pressBack();
    }

    private static String[] generateRandomWords(int numberOfWords)
    {
        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for(int i = 0; i < numberOfWords; i++)
        {
            char[] word = new char[random.nextInt(5)+3];
            for(int j = 0; j < word.length; j++)
            {
                word[j] = (char)('a' + random.nextInt(26));
            }
            randomStrings[i] = new String(word);
        }
        return randomStrings;
    }
}
