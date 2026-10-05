package com.skillbridge.dao;

/** Thrown when someone else booked the same worker and time while the customer was paying. */
public class SlotTakenException extends Exception {
    private static final long serialVersionUID = 1L;

    public SlotTakenException(String message) {
        super(message);
    }
}
