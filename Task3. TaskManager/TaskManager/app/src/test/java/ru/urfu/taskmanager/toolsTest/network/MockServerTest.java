package ru.urfu.taskmanager.toolsTest.network;

import android.graphics.Color;
import android.test.mock.MockContext;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

import ru.urfu.taskmanager.entities.User;
import ru.urfu.taskmanager.db.DbTasks;
import ru.urfu.taskmanager.network.APICallback;
import ru.urfu.taskmanager.network.APIResponse;
import ru.urfu.taskmanager.network.APIService;
import ru.urfu.taskmanager.entities.TaskEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.urfu.taskmanager.network.APIResponse.STATUS_OK;

public class MockServerTest
{
    private static final String USERNAME = "TestUser";

    private MockContext mockContext;
    private MockServer<TaskEntry> mockServer;
    private MockAPIService<TaskEntry> mockService;

    private TaskEntry entry;

    @Before
    public void setUp() throws IOException {
        mockContext = new MockContext();

        DbTasks.init(mockContext);
        User.doLogin(mockContext, USERNAME);

        mockServer = new MockServer<>();
        mockService = MockAPIService.mock(mockServer,
                new APIService<TaskEntry>(User.getActiveUser())
                        .setRawType(TaskEntry.class));

        long time = System.currentTimeMillis();

        entry = new TaskEntry()
                .setTitle("Title")
                .setDescription("Description 1")
                .setCreated(time)
                .setEdited(time)
                .setTtl(time)
                .setColor(Color.BLACK);
    }

    @Test
    public void mockApiServiceTest() {
        mockService.createNote(entry).send(new APICallback<Integer>()
        {
            @Override
            public void onResponse(APIResponse<Integer> response) {
                assertEquals(response.getStatus(), STATUS_OK);
                assertNotNull(response.getBody());

                mockService.getNoteById(response.getBody()).send(new APICallback<TaskEntry>()
                {
                    @Override
                    public void onResponse(APIResponse<TaskEntry> response) {
                        assertEquals(response.getStatus(), STATUS_OK);
                        assertEquals(response.getBody().hashCode(), entry.hashCode());

                        final String editedTitle = "Edited title";
                        mockService.editNote(entry.setTitle(editedTitle), 0).send(new APICallback<Void>()
                        {
                            @Override
                            public void onResponse(APIResponse<Void> response) {
                                assertEquals(response.getStatus(), STATUS_OK);

                                mockService.deleteNote(0).send(new APICallback<Void>()
                                {
                                    @Override
                                    public void onResponse(APIResponse<Void> response) {
                                        assertEquals(response.getStatus(), STATUS_OK);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void getUserNotes(){
        mockService.getUserNotes().send(new APICallback<Collection<TaskEntry>>()
        {
            @Override
            public void onResponse(APIResponse<Collection<TaskEntry>> response) {
                assertEquals(response.getStatus(), STATUS_OK);
                assertNotNull(response.getBody());
            }
        });
    }
}
