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
    private static final Locale sLocale = new Locale("ru");
    private static final SimpleDateFormat sFormatter = new SimpleDateFormat("dd.MM.yyyy", sLocale);

    public static HoursAndMinutes getHoursAndMinutesFromUnix(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        HoursAndMinutes time = new HoursAndMinutes();
        time.hours = calendar.get(Calendar.HOUR_OF_DAY);
        time.minutes = calendar.get(Calendar.MINUTE);

        return time;
    }

    public static String format(Calendar date) {
        String[] newMonths = {"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(sLocale);
        dfs.setMonths(newMonths);

        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, sLocale);
        SimpleDateFormat sdf = (SimpleDateFormat) df;
        sdf.setDateFormatSymbols(dfs);

        Date jud = null;
        try {
            jud = sFormatter.parse(sFormatter.format(date.getTime()));
        } catch (ParseException ignored) {
        }

        return sdf.format(jud); // output: 28 февраля 2014 г.
    }

    public static Date parse(String text) throws ParseException {
        return sFormatter.parse(text);
    }

    public static String format(Date date) {
        return sFormatter.format(date);
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
