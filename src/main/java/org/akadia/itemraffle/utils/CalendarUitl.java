package org.akadia.itemraffle.utils;

import org.akadia.itemraffle.ItemRaffleMain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarUitl {
    private static final String pattern = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public static String formatMillis(long millis) {
        return simpleDateFormat.format(new Date(millis));
    }

    /**
     * @param seconds integer in seconds
     * @return days:hours:minutes:seconds
     */
    public static String formatSeconds(ItemRaffleMain main, int seconds) {
        if (seconds < 0) {
            return main.getLocale("gui.expiredTimer");
        }
        int minute = seconds / 60;
        int remainderSecond = seconds % 60;

        int hour = minute / 60;
        int remainderMinute = minute % 60;

        int day = hour / 24;
        int remainderHour = hour % 24;

        return (day < 10 ? "0" + day : day) + main.getLocale("gui.day") +
                (remainderHour < 10 ? "0" + remainderHour : remainderHour) + main.getLocale("gui.hour") +
                (remainderMinute < 10 ? "0" + remainderMinute : remainderMinute) + main.getLocale("gui.minute") +
                (remainderSecond < 10 ? "0" + remainderSecond : remainderSecond) + main.getLocale("gui.second");
    }
}
