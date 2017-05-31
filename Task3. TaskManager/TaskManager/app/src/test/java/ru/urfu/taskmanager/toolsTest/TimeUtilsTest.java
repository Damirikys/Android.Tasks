package ru.urfu.taskmanager.toolsTest;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import ru.urfu.taskmanager.utils.tools.TimeUtils;

import static org.junit.Assert.assertEquals;

public class TimeUtilsTest
{
    private final Long nowTime = 1355252400000L;
    private final String secondFormatCorrectResult = "12.12.2012";
    private final String firstFormatCorrectResult = "12 декабря 2012 г.";

    private Calendar calendar;

    @Before
    public void initialize() {
        calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setLenient(false);
        calendar.setTimeInMillis(nowTime);
    }

    @Test
    public void firstFromatIsOk() {
        assertEquals(firstFormatCorrectResult, TimeUtils.format(calendar));
    }

    @Test
    public void secondFormatIsOk() {
        assertEquals(secondFormatCorrectResult, TimeUtils.format(calendar.getTime()));
    }

    @Test
    public void dateParserIsOk() throws ParseException {
        assertEquals(secondFormatCorrectResult, TimeUtils.format(
                TimeUtils.parse(secondFormatCorrectResult)
        ));
    }
}
