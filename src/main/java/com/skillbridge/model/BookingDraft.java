package com.skillbridge.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A booking that has been chosen but not paid for yet. It lives in the HttpSession
 * (session management demo) until the customer completes the payment step.
 */
public class BookingDraft implements Serializable {
    private static final long serialVersionUID = 1L;

    private int workerId;
    private LocalDate date;
    private LocalTime start;
    private int hours;
    private String address;
    private String notes;

    public BookingDraft(int workerId, LocalDate date, LocalTime start, int hours, String address, String notes) {
        this.workerId = workerId;
        this.date = date;
        this.start = start;
        this.hours = hours;
        this.address = address;
        this.notes = notes;
    }

    public int getWorkerId() { return workerId; }
    public LocalDate getDate() { return date; }
    public LocalTime getStart() { return start; }
    public int getHours() { return hours; }
    public String getAddress() { return address; }
    public String getNotes() { return notes; }
    public LocalTime getEnd() { return start.plusHours(hours); }
}
