package ru.urfu.taskmanager.toolsTest;

import org.junit.Test;

import java.text.ParseException;

import ru.urfu.taskmanager.utils.tools.ISO8601;

import static org.junit.Assert.assertEquals;

public class IsoFormatTest
{
    private final long timestamp = 1495773283000L;
    private final String isoString = "2017-05-26T09:34:43+05:00";

    @Test
    public void isoFromTimestamp() {
        assertEquals(isoString, ISO8601.fromTimestamp(timestamp));
    }

    @Test
    public void isoToTimestamp() throws ParseException {
        assertEquals(timestamp, ISO8601.toTimestamp(isoString));
    }
}