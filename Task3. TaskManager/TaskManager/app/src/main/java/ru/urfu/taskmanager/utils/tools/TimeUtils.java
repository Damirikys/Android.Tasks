package ru.urfu.taskmanager.utils.tools;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public final class TimeUtils
{
    private static final Locale locale = new Locale("ru");
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", locale);

    public static class HoursAndMinutes
    {
        int hours, minutes;

        @Override
        public String toString() {
            return (hours < 10 ? "0" + hours : hours) + ":" +
                    (minutes < 10 ? "0" + minutes : minutes);
        }
    }

    public static HoursAndMinutes getHoursAndMinutesFromUnix(long timestamp)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(timestamp);

        HoursAndMinutes time = new HoursAndMinutes();
        time.hours = calendar.get(Calendar.HOUR_OF_DAY);
        time.minutes = calendar.get(Calendar.MINUTE);

        return time;
    }

    public static String format(Calendar date) {
        String[] newMonths = {
                "января", "февраля", "марта", "апреля", "мая", "июня",
                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
        dfs.setMonths(newMonths);

        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, locale);
        SimpleDateFormat sdf = (SimpleDateFormat) df;
        sdf.setDateFormatSymbols(dfs);

        Date jud = null;
        try {
            jud = new SimpleDateFormat("yyyy-MM-dd", locale)
                    .parse(formatter.format(date.getTime()));
        } catch (ParseException ignored) {}

        return sdf.format(jud); // output: 28 февраля 2014 г.
    }
}
