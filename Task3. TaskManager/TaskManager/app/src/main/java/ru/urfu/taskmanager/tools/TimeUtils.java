package ru.urfu.taskmanager.tools;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils
{
    private static final Locale LOCALE = new Locale("ru");
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM.yyyy", LOCALE);
    private static final String[] MONTHS_NAMES = {
            "января", "февраля", "марта",
            "апреля", "мая", "июня",
            "июля", "августа", "сентября",
            "октября", "ноября", "декабря"
    };

    private TimeUtils() {}

    public synchronized static HoursAndMinutes getHoursAndMinutesFromUnix(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setLenient(false);

        calendar.setTimeInMillis(timestamp);

        HoursAndMinutes time = new HoursAndMinutes();
        time.hours = calendar.get(Calendar.HOUR_OF_DAY);
        time.minutes = calendar.get(Calendar.MINUTE);

        return time;
    }

    public synchronized static String format(Calendar date) {
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(LOCALE);
        dfs.setMonths(MONTHS_NAMES);

        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, LOCALE);
        SimpleDateFormat sdf = (SimpleDateFormat) df;
        sdf.setDateFormatSymbols(dfs);

        Date jud = null;
        try {
            jud = FORMATTER.parse(FORMATTER.format(date.getTime()));
        } catch (ParseException ignored) {
        }

        return sdf.format(jud); // output: 28 февраля 2014 г.
    }

    public synchronized static Date parse(String text) throws ParseException {
        return FORMATTER.parse(text);
    }

    public synchronized static String format(Date date) {
        return FORMATTER.format(date);
    }

    public static class HoursAndMinutes
    {
        int hours, minutes;

        @Override
        public String toString() {
            return (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes);
        }
    }
}
