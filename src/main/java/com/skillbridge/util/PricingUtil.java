package com.skillbridge.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Booking price rules: total = rate x hours + 5% platform fee. */
public final class PricingUtil {
    public static final int MIN_HOURS = 1;
    public static final int MAX_HOURS = 8;
    public static final int MIN_RATE = 50;
    public static final int MAX_RATE = 2000;
    public static final BigDecimal FEE_PERCENT = new BigDecimal("5");

    private PricingUtil() {
    }

    public static BigDecimal subtotal(BigDecimal hourlyRate, int hours) {
        return hourlyRate.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal platformFee(BigDecimal subtotal) {
        return subtotal.multiply(FEE_PERCENT).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal total(BigDecimal hourlyRate, int hours) {
        BigDecimal sub = subtotal(hourlyRate, hours);
        return sub.add(platformFee(sub));
    }
}
