package com.skillbridge.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class PricingAndSlotTest {
    private static final LocalTime OPEN = LocalTime.of(8, 0);
    private static final LocalTime CLOSE = LocalTime.of(18, 0);
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 1, 10, 0);
    private static final LocalDate TOMORROW = NOW.toLocalDate().plusDays(1);

    @Test
    void pricingAddsFivePercent() {
        BigDecimal rate = new BigDecimal("450.00");
        assertEquals(new BigDecimal("900.00"), PricingUtil.subtotal(rate, 2));
        assertEquals(new BigDecimal("45.00"), PricingUtil.platformFee(new BigDecimal("900.00")));
        assertEquals(new BigDecimal("945.00"), PricingUtil.total(rate, 2));
    }

    @Test
    void pricingRoundsToPaise() {
        // 5% of 1050.00 = 52.50 ; 5% of 333.00 = 16.65
        assertEquals(new BigDecimal("52.50"), PricingUtil.platformFee(new BigDecimal("1050.00")));
        assertEquals(new BigDecimal("16.65"), PricingUtil.platformFee(new BigDecimal("333.00")));
    }

    @Test
    void validBooking() {
        assertNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(9, 0), 2, NOW));
    }

    @Test
    void durationBoundaries() {
        assertNotNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(9, 0), 0, NOW));
        assertNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(9, 0), 1, NOW));
        assertNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(9, 0), 8, NOW));
        assertNotNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(9, 0), 9, NOW));
    }

    @Test
    void workingHoursBoundaries() {
        assertNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(8, 0), 2, NOW));    // exactly at opening
        assertNotNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(7, 30), 2, NOW));
        assertNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(16, 0), 2, NOW));   // ends exactly at closing
        assertNotNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(16, 30), 2, NOW)); // ends 30 min after closing
    }

    @Test
    void dateBoundaries() {
        LocalDate today = NOW.toLocalDate();
        assertNotNull(SlotRules.check(OPEN, CLOSE, today.minusDays(1), LocalTime.of(9, 0), 2, NOW));
        assertNull(SlotRules.check(OPEN, CLOSE, today.plusDays(SlotRules.MAX_DAYS_AHEAD), LocalTime.of(9, 0), 2, NOW));
        assertNotNull(SlotRules.check(OPEN, CLOSE, today.plusDays(SlotRules.MAX_DAYS_AHEAD + 1), LocalTime.of(9, 0), 2, NOW));
    }

    @Test
    void sameDayNeedsOneHourNotice() {
        LocalDate today = NOW.toLocalDate();
        assertNotNull(SlotRules.check(OPEN, CLOSE, today, LocalTime.of(10, 30), 2, NOW));
        assertNull(SlotRules.check(OPEN, CLOSE, today, LocalTime.of(11, 0), 2, NOW));
    }

    @Test
    void startMustBeOnTheHalfHour() {
        assertNotNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(9, 15), 2, NOW));
        assertNull(SlotRules.check(OPEN, CLOSE, TOMORROW, LocalTime.of(9, 30), 2, NOW));
    }

    @Test
    void missingInputs() {
        assertNotNull(SlotRules.check(OPEN, CLOSE, null, LocalTime.of(9, 0), 2, NOW));
        assertNotNull(SlotRules.check(OPEN, CLOSE, TOMORROW, null, 2, NOW));
    }
}
