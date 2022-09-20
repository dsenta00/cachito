package dsenta.cachito.utils;

import dsenta.cachito.exception.attribute.AttributeValueTypeMismatchException;
import dsenta.cachito.model.attribute.DataType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public final class DateIso8601 {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private DateIso8601() {
        DATE_TIME_FORMAT.setTimeZone(UTC);
    }

    public static String toString(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }

    public static Date fromString(String string) {
        try {
            return DATE_TIME_FORMAT.parse(string);
        } catch (ParseException e1) {
            try {
                return DATE_FORMAT.parse(string);
            } catch (ParseException e2) {
                try {
                    return Date.from(Instant.ofEpochMilli(Long.parseLong(string)));
                } catch (Exception e) {
                    throw new AttributeValueTypeMismatchException(DataType.DATE, string);
                }
            }
        }
    }
}