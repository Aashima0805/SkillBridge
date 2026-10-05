package com.skillbridge.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Pure booking-time rules (no database), so they can be unit tested easily.
 * Returns null when the request is fine, otherwise a human-readable message.
 */
public final class SlotRules {
    public static final int MAX_DAYS_AHEAD = 60;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private SlotRules() {
    }

    public static String label(LocalTime t) {
        return t.format(FMT);
    }

    public static String check(LocalTime workStart, LocalTime workEnd, LocalDate date, LocalTime start,
                               int hours, LocalDateTime now) {
        if (hours < PricingUtil.MIN_HOURS || hours > PricingUtil.MAX_HOURS) {
            return "Duration must be between " + PricingUtil.MIN_HOURS + " and " + PricingUtil.MAX_HOURS + " hours.";
        }
        if (date == null || start == null) {
            return "Please choose a date and a start time.";
        }
        LocalDate today = now.toLocalDate();
        if (date.isBefore(today)) {
            return "The date cannot be in the past.";
        }
        if (date.isAfter(today.plusDays(MAX_DAYS_AHEAD))) {
            return "You can book only within the next " + MAX_DAYS_AHEAD + " days.";
        }
        if (start.getMinute() != 0 && start.getMinute() != 30) {
            return "Start time must be on the hour or half hour.";
        }
        int startMin = start.getHour() * 60 + start.getMinute();
        int endMin = startMin + hours * 60;
        int workStartMin = workStart.getHour() * 60 + workStart.getMinute();
        int workEndMin = workEnd.getHour() * 60 + workEnd.getMinute();
        if (startMin < workStartMin || endMin > workEndMin) {
            return "Outside working hours (" + label(workStart) + " to " + label(workEnd) + ").";
        }
        if (LocalDateTime.of(date, start).isBefore(now.plusHours(1))) {
            return "Same-day bookings must start at least 1 hour from now.";
        }
        return null;
    }

    public static LocalTime endTime(LocalTime start, int hours) {
        return start.plusHours(hours);
    }
}
