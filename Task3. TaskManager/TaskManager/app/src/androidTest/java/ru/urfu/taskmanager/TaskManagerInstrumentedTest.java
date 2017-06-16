package ru.urfu.taskmanager;

import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;

import java.util.Random;

import ru.urfu.taskmanager.db.DbTasks;
import ru.urfu.taskmanager.entities.TaskEntry;
import ru.urfu.taskmanager.entities.User;
import ru.urfu.taskmanager.mockserver.MockAPIService;
import ru.urfu.taskmanager.mockserver.MockServer;
import ru.urfu.taskmanager.network.APIService;
import ru.urfu.taskmanager.view.main.view.TaskManagerActivity_;

abstract class TaskManagerInstrumentedTest
{
    protected static final String PACKAGE = "ru.urfu.taskmanager";
    private static final long TIMEOUT = 1000;
    private static final String TEST_USER_NAME = "TestUser";

    @Rule
    public ActivityTestRule<TaskManagerActivity_> mActivityRule =
            new ActivityTestRule<TaskManagerActivity_>(TaskManagerActivity_.class)
    {
        @Override
        protected void beforeActivityLaunched() {
            User currentUser = User.doLogin(InstrumentationRegistry.getTargetContext(), TEST_USER_NAME);

            MockServer<TaskEntry> mockServer = new MockServer<>();
            MockAPIService<TaskEntry> mockService = MockAPIService.mock(mockServer,
                    new APIService<TaskEntry>(currentUser).setRawType(TaskEntry.class));

            currentUser.changeApiService(InstrumentationRegistry.getTargetContext(), mockService);

            long time = System.currentTimeMillis();
            TaskEntry entry = new TaskEntry()
                    .setTitle("Title")
                    .setDescription("Description")
                    .setCreated(time)
                    .setEdited(time)
                    .setTtl(time + 8460000)
                    .setImageUrl("")
                    .setEntryId(0)
                    .setColor(Color.BLACK);

            DbTasks.init(InstrumentationRegistry.getTargetContext());
            DbTasks db = DbTasks.getInstance();
            db.insertEntry(entry.setTitle(generateRandomWords(2)).setDescription(generateRandomWords(3)));
            db.insertEntry(entry.setTitle(generateRandomWords(2)).setDescription(generateRandomWords(3)));
            db.insertEntry(entry.setTitle(generateRandomWords(2)).setDescription(generateRandomWords(3)));

            super.beforeActivityLaunched();
        }
    };

    private static String generateRandomWords(int numberOfWords)
    {
        String[] randomStrings = new String[numberOfWords];
        Random random = new Random();
        for (int i = 0; i < numberOfWords; i++) {
            char[] word = new char[random.nextInt(5)+3];
            for (int j = 0; j < word.length; j++) {
                word[j] = (char) ('a' + random.nextInt(26));
            }

            randomStrings[i] = new String(word);
        }

        StringBuilder builder = new StringBuilder();
        for (String str : randomStrings) {
            builder.append(str);
            builder.append(" ");
        }

        return builder.toString().trim();
    }

    public void waitView() {
        SystemClock.sleep(TIMEOUT);
    }

    public String getString(@StringRes int resId) {
        return mActivityRule.getActivity().getString(resId);
    }
}
