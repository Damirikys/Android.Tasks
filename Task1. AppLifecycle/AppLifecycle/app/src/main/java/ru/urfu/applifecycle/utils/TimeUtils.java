package ru.urfu.applifecycle.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils
{
    public static String getTimeRepresentationFromUnix(long time)
    {
        Date d = new Date(time);
        DateFormat f = SimpleDateFormat.getTimeInstance();
        f.setTimeZone(TimeZone.getTimeZone("GMT"));
        return f.format(d);
    }
}
