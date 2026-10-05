package com.skillbridge.util;

import java.time.YearMonth;
import java.util.regex.Pattern;

/**
 * Server-side validation rules. The same rules are mirrored in assets/js/validate.js
 * so the browser gives instant feedback, but the server never trusts the browser.
 */
public final class Validator {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{3,19}$");
    private static final Pattern PERSON_NAME = Pattern.compile("^\\p{L}[\\p{L} .'-]{1,59}$");
    private static final Pattern CITY = Pattern.compile("^\\p{L}[\\p{L} .'-]{1,39}$");
    private static final Pattern UPI = Pattern.compile("^[A-Za-z0-9._-]{2,64}@[A-Za-z]{2,32}$");
    private static final Pattern CVV = Pattern.compile("^[0-9]{3}$");
    private static final Pattern EXPIRY = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");

    private Validator() {
    }

    /** Trim; null becomes empty string. */
    public static String clean(String s) {
        return s == null ? "" : s.trim();
    }

    public static boolean isValidEmail(String s) {
        return s != null && s.length() <= 100 && EMAIL.matcher(s).matches();
    }

    /** Indian mobile number: 10 digits, starting with 6, 7, 8 or 9. */
    public static boolean isValidPhone(String s) {
        return s != null && PHONE.matcher(s).matches();
    }

    /** 4 to 20 characters: letters, digits, underscore; must start with a letter. */
    public static boolean isValidUsername(String s) {
        return s != null && USERNAME.matcher(s).matches();
    }

    public static boolean isValidPersonName(String s) {
        return s != null && PERSON_NAME.matcher(s).matches();
    }

    public static boolean isValidCity(String s) {
        return s != null && CITY.matcher(s).matches();
    }

    /** 8 to 64 characters with at least one letter and one digit. */
    public static boolean isValidPassword(String s) {
        if (s == null || s.length() < 8 || s.length() > 64) {
            return false;
        }
        boolean letter = false;
        boolean digit = false;
        for (char ch : s.toCharArray()) {
            if (Character.isLetter(ch)) {
                letter = true;
            } else if (Character.isDigit(ch)) {
                digit = true;
            }
        }
        return letter && digit;
    }

    public static boolean inRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean lengthBetween(String s, int min, int max) {
        return s != null && s.length() >= min && s.length() <= max;
    }

    /** Card number: exactly 16 digits (spaces removed by the caller) and a valid Luhn checksum. */
    public static boolean isValidCardNumber(String digits) {
        if (digits == null || !digits.matches("[0-9]{16}")) {
            return false;
        }
        int sum = 0;
        boolean dbl = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (dbl) {
                d *= 2;
                if (d > 9) {
                    d -= 9;
                }
            }
            sum += d;
            dbl = !dbl;
        }
        return sum % 10 == 0;
    }

    /** Expiry in MM/YY format, not earlier than the current month. */
    public static boolean isValidExpiry(String s, YearMonth now) {
        if (s == null) {
            return false;
        }
        var m = EXPIRY.matcher(s);
        if (!m.matches()) {
            return false;
        }
        YearMonth ym = YearMonth.of(2000 + Integer.parseInt(m.group(2)), Integer.parseInt(m.group(1)));
        return !ym.isBefore(now);
    }

    public static boolean isValidCvv(String s) {
        return s != null && CVV.matcher(s).matches();
    }

    public static boolean isValidUpi(String s) {
        return s != null && UPI.matcher(s).matches();
    }

    /** Parses an int, returning fallback if the text is not a number. */
    public static int toInt(String s, int fallback) {
        try {
            return Integer.parseInt(clean(s));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
