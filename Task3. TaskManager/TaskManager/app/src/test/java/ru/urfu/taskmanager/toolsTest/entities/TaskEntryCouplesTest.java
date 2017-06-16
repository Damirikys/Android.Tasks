package ru.urfu.taskmanager.toolsTest.entities;

import org.junit.Before;
import org.junit.Test;

import ru.urfu.taskmanager.entities.TaskEntry;
import ru.urfu.taskmanager.entities.TaskEntryCouples;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TaskEntryCouplesTest
{
    private TaskEntryCouples couples;

    @Before
    public void setUp() {
        couples = new TaskEntryCouples();
        couples.put(new TaskEntry().setEntryId(1), new TaskEntry().setEntryId(2));
        couples.put(new TaskEntry().setEntryId(1), new TaskEntry().setEntryId(2));
        couples.put(new TaskEntry().setEntryId(1), new TaskEntry().setEntryId(2));
        couples.put(new TaskEntry().setEntryId(1), new TaskEntry().setEntryId(2));
    }

    @Test
    public void correctCouples() {
        assertEquals(couples.size(), 4);
        assertEquals(couples.isEmpty(), false);
        couples.remove(0);
        assertEquals(couples.size(), 3);

        for (TaskEntryCouples.Couple couple : couples) {
            assertNotEquals(couple.getKey(), couple.getValue());
            assertEquals((int) couple.getKey().getEntryId(), 1);
            assertEquals((int) couple.getValue().getEntryId(), 2);
        }
    }
}
