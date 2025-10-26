package com.example.visittracking.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Pavel Zhurenkov
 */
public class TimestampConverter {
    public static String convertTimestampToTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    // Более подробное преобразование
    public static String getDetailedTimeInfo(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        return String.format(
                "Time: %02d:%02d:%02d | Date: %s",
                dateTime.getHour(),
                dateTime.getMinute(),
                dateTime.getSecond(),
                dateTime.toLocalDate().toString()
        );
    }
}
