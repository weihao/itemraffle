package org.akadia.itemraffle.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarUitl {
    private static final String pattern = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public static String formatMillis(long millis) {
        return simpleDateFormat.format(new Date(millis));
    }

    public static String formatSeconds(int seconds) {
//        days:hours:minutes:seconds
        int minute = seconds / 60;
        int remainderSecond = seconds % 60;

        int hour = minute / 60;
        int remainderMinute = minute % 60;

        int day = hour / 24;
        int remainderHour = hour % 24;

        return (day < 10 ? "0" + day : day) + ":" +
                (remainderHour < 10 ? "0" + remainderHour : remainderHour) + ":" +
                (remainderMinute < 10 ? "0" + remainderMinute : remainderMinute) + ":" +
                (remainderSecond < 10 ? "0" + remainderSecond : remainderSecond);
    }
}
