package ru.urfu.taskmanager.toolsTest.entities;

import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;

import ru.urfu.taskmanager.entities.TaskEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TaskEntryTest
{
    private TaskEntry first;
    private TaskEntry second;

    @Before
    public void setUp() {
        long time = System.currentTimeMillis();

        first = new TaskEntry()
                .setTitle("Title")
                .setDescription("Description 1")
                .setCreated(time)
                .setEdited(time)
                .setTtl(time)
                .setColor(Color.BLACK)
                .setEntryId(1);

        second = new TaskEntry()
                .setTitle("Title")
                .setDescription("Description 2")
                .setCreated(time)
                .setEdited(time)
                .setTtl(time)
                .setColor(Color.BLUE)
                .setEntryId(2);
    }

    @Test
    public void correctFields() {
        assertNotEquals(first, second);
        assertNotEquals(first.getDescription(), second.getDescription());
        assertNotEquals(first.getColor(), second.getColor());

        assertEquals(first.getTitle(), second.getTitle());
        assertEquals(first.getTtl(), second.getTtl());
        assertEquals(first.getCreated(), second.getCreated());
        assertEquals(first.getEdited(), second.getEdited());
    }

    @Test
    public void correctEqualsHashcode() {
        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());

        second.setDescription(first.getDescription());
        second.setColor(Color.BLACK);
        second.setEntryId(first.getEntryId());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first.toString(), second.toString());
    }
}
