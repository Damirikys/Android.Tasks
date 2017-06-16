package ru.urfu.taskmanager.toolsTest.entities;

import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import ru.urfu.taskmanager.entities.User;
import ru.urfu.taskmanager.db.DbTasks;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserTest
{
    private static final String USERNAME = "TestUser";
    private MockContext mockContext;

    @Before
    public void setUp() {
        mockContext = new MockContext();
        DbTasks.init(mockContext);

        User.doLogin(mockContext, USERNAME);
    }

    @Test
    public void checkCorrectModelAfterUserChange() {
        assertEquals(User.getActiveUser().getLogin(), USERNAME);

        int userId = User.getActiveUser().getUserId();
        String deviceIdentifier = User.getActiveUser().getDeviceIdentifier();

        User.doLogin(mockContext, "OtherUserName");

        assertNotEquals(User.getActiveUser().getLogin(), USERNAME);
        assertNotEquals(User.getActiveUser().getUserId(), userId);
        assertEquals(User.getActiveUser().getDeviceIdentifier(), deviceIdentifier);
    }
}
