package com.skillbridge.util;

import java.security.SecureRandom;

/**
 * Simulated payment gateway for the lab. No real money moves and card data is never stored.
 * Test rules: a card number ending in 0002 is declined, and a UPI id ending in @fail is declined.
 */
public final class MockGateway {
    private static final SecureRandom RANDOM = new SecureRandom();

    private MockGateway() {
    }

    public record Result(boolean success, String message, String ref, String detail) {
    }

    public static Result charge(String method, String cardDigits, String upiId) {
        boolean card = "CARD".equals(method);
        String detail = card ? "Card ****" + cardDigits.substring(cardDigits.length() - 4) : "UPI: " + upiId;
        if (card && cardDigits.endsWith("0002")) {
            return new Result(false, "Payment declined by the card issuer (test card).", null, detail);
        }
        if (!card && upiId.toLowerCase().endsWith("@fail")) {
            return new Result(false, "UPI request was declined (test id).", null, detail);
        }
        String ref = "SB" + Long.toString(System.currentTimeMillis(), 36).toUpperCase() + (1000 + RANDOM.nextInt(9000));
        return new Result(true, "Payment successful.", ref, detail);
    }
}
