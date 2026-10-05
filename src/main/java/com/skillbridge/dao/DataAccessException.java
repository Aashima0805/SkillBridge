package com.skillbridge.dao;

/** Unchecked wrapper around SQLException so servlets stay readable. */
public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
