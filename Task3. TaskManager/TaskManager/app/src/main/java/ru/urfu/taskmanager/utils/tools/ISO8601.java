package ru.urfu.taskmanager.utils.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Helper class for handling a most common subset of ISO 8601 strings
 * (in the following format: "2008-03-01T13:00:00+01:00"). It supports
 * parsing the "Z" timezone, but many other less-used features are
 * missing.
 */
public final class ISO8601 {
    private static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ROOT);

    /**
     * Transform Calendar to ISO 8601 string.
     */
    public static String fromTimestamp(final long timestamp) {
        Date date = new Date(timestamp);
        String formatted = formatter.format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * Transform ISO 8601 string to Calendar.
     */
    public static long toTimestamp(final String iso8601string)
            throws ParseException {
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException("Invalid length", 0);
        }
        return formatter.parse(s).getTime();
    }
}