package com.skillbridge.service;

import com.skillbridge.dao.BookingDAO;
import com.skillbridge.dao.WorkerDAO;
import com.skillbridge.model.Booking;
import com.skillbridge.model.Worker;
import com.skillbridge.util.Config;
import com.skillbridge.util.SlotRules;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Business rules for booking a worker: combines the pure time rules with the database check. */
public class BookingService {
    private final WorkerDAO workers = new WorkerDAO();
    private final BookingDAO bookings = new BookingDAO();

    public record Check(boolean ok, String message, Worker worker, LocalDate date, LocalTime start, int hours) {
    }

    private Check fail(String message, Worker w) {
        return new Check(false, message, w, null, null, 0);
    }

    public Check check(int workerId, String dateText, String startText, int hours) {
        Worker w = workers.findById(workerId);
        if (w == null || !w.isVerified() || !w.isUserActive()) {
            return fail("This worker is not available for booking.", w);
        }
        if (!w.isAvailable()) {
            return fail(w.getFullName() + " is not accepting bookings right now.", w);
        }
        LocalDate date;
        LocalTime start;
        try {
            date = LocalDate.parse(dateText);
            start = LocalTime.parse(startText);
        } catch (DateTimeParseException | NullPointerException e) {
            return fail("Please choose a valid date and start time.", w);
        }
        String error = SlotRules.check(w.getWorkStart(), w.getWorkEnd(), date, start, hours,
                LocalDateTime.now(Config.zone()));
        if (error != null) {
            return fail(error, w);
        }
        LocalTime end = start.plusHours(hours);
        if (!bookings.isSlotFree(w.getId(), date, start, end)) {
            List<Booking> busy = bookings.busySlots(w.getId(), date);
            StringBuilder sb = new StringBuilder("Already booked on this day:");
            for (Booking b : busy) {
                sb.append(' ').append(b.getStartLabel()).append(" - ").append(b.getEndLabel()).append(';');
            }
            sb.append(" please choose another time.");
            return fail(sb.toString(), w);
        }
        return new Check(true, "Great, this slot is free.", w, date, start, hours);
    }
}
