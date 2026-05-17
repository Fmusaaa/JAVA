package com.taskbot.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateTimeUtil {
    public static final DateTimeFormatter USER_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateTimeUtil() {
    }

    public static LocalDateTime parseUserDateTime(String value) {
        try {
            return LocalDateTime.parse(value.trim(), USER_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format tanggal salah. Contoh: 15/03/2025 14:30");
        }
    }

    public static String formatUserDateTime(LocalDateTime value) {
        return value == null ? "Tidak ada due date" : value.format(USER_FORMATTER);
    }

    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX).withNano(0);
    }
}
