package com.skillbridge.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Black-box and boundary-value tests for the validation rules (Experiment 10). */
class ValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"9876543210", "6000000000", "7999999999", "8123456789"})
    void validPhones(String p) { assertTrue(Validator.isValidPhone(p)); }

    @ParameterizedTest
    @ValueSource(strings = {"5876543210", "987654321", "98765432101", "98765abcde", "", " 9876543210"})
    void invalidPhones(String p) { assertFalse(Validator.isValidPhone(p)); }

    @Test
    void phoneNull() { assertFalse(Validator.isValidPhone(null)); }

    @Test
    void usernameLengthBoundaries() {
        assertFalse(Validator.isValidUsername("abc"));            // 3 chars: too short
        assertTrue(Validator.isValidUsername("abcd"));            // 4 chars: minimum
        assertTrue(Validator.isValidUsername("a".repeat(20)));    // 20 chars: maximum
        assertFalse(Validator.isValidUsername("a".repeat(21)));   // 21 chars: too long
    }

    @Test
    void usernameRules() {
        assertFalse(Validator.isValidUsername("1abcd"));
        assertFalse(Validator.isValidUsername("ab cd"));
        assertFalse(Validator.isValidUsername("ab-cd"));
        assertTrue(Validator.isValidUsername("ab_cd9"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a@b.co", "first.last+tag@mail.example.com", "USER_1@site.in"})
    void validEmails(String e) { assertTrue(Validator.isValidEmail(e)); }

    @ParameterizedTest
    @ValueSource(strings = {"plain", "a@b", "@b.com", "a@.com", "a b@c.com", "a@b.c"})
    void invalidEmails(String e) { assertFalse(Validator.isValidEmail(e)); }

    @Test
    void passwordBoundaries() {
        assertFalse(Validator.isValidPassword("abc1234"));              // 7 chars
        assertTrue(Validator.isValidPassword("abc12345"));              // 8 chars
        assertTrue(Validator.isValidPassword("a1" + "x".repeat(62)));   // 64 chars
        assertFalse(Validator.isValidPassword("a1" + "x".repeat(63)));  // 65 chars
        assertFalse(Validator.isValidPassword("abcdefgh"));             // no digit
        assertFalse(Validator.isValidPassword("12345678"));             // no letter
    }

    @Test
    void personNameAndCity() {
        assertTrue(Validator.isValidPersonName("Ramesh Kumar"));
        assertTrue(Validator.isValidPersonName("Anne-Marie O'Neil"));
        assertFalse(Validator.isValidPersonName("R"));
        assertFalse(Validator.isValidPersonName("Ramesh123"));
        assertTrue(Validator.isValidCity("Hyderabad"));
        assertFalse(Validator.isValidCity("City 9"));
    }

    @Test
    void cardNumberLuhn() {
        assertTrue(Validator.isValidCardNumber("4242424242424242"));
        assertFalse(Validator.isValidCardNumber("4242424242424241"));   // bad checksum
        assertFalse(Validator.isValidCardNumber("424242424242424"));    // 15 digits
        assertFalse(Validator.isValidCardNumber("42424242424242421"));  // 17 digits
        assertFalse(Validator.isValidCardNumber("4242 4242 4242 4242")); // spaces must be removed first
    }

    @Test
    void expiryBoundaries() {
        YearMonth now = YearMonth.of(2026, 10);
        assertTrue(Validator.isValidExpiry("10/26", now));   // current month is still valid
        assertFalse(Validator.isValidExpiry("09/26", now));  // last month
        assertTrue(Validator.isValidExpiry("01/27", now));
        assertFalse(Validator.isValidExpiry("13/27", now));  // month out of range
        assertFalse(Validator.isValidExpiry("00/27", now));
        assertFalse(Validator.isValidExpiry("1/27", now));
    }

    @Test
    void cvvAndUpi() {
        assertTrue(Validator.isValidCvv("123"));
        assertFalse(Validator.isValidCvv("12"));
        assertFalse(Validator.isValidCvv("1234"));
        assertTrue(Validator.isValidUpi("name@okaxis"));
        assertFalse(Validator.isValidUpi("name@"));
        assertFalse(Validator.isValidUpi("name okaxis"));
    }

    @Test
    void helpers() {
        assertEquals("", Validator.clean(null));
        assertEquals("x", Validator.clean("  x "));
        assertEquals(7, Validator.toInt(" 7 ", 0));
        assertEquals(-1, Validator.toInt("abc", -1));
        assertTrue(Validator.inRange(5, 1, 5));
        assertFalse(Validator.inRange(6, 1, 5));
        assertTrue(Validator.lengthBetween("abcd", 4, 4));
    }
}
