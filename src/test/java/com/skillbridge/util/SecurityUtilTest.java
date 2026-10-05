package com.skillbridge.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SecurityUtilTest {
    @Test
    void passwordHashVerifies() {
        String hash = PasswordUtil.hash("Secret123");
        assertNotEquals("Secret123", hash);
        assertTrue(PasswordUtil.verify("Secret123", hash));
        assertFalse(PasswordUtil.verify("secret123", hash));
    }

    @Test
    void rememberTokenRoundTrip() {
        long now = 1_800_000_000L;
        String token = TokenUtil.create(42, now);
        assertEquals(42, TokenUtil.parse(token, now + 60));
    }

    @Test
    void rememberTokenRejectsTamperingAndExpiry() {
        long now = 1_800_000_000L;
        String token = TokenUtil.create(42, now);
        String tampered = token.replaceFirst("^42", "43");
        assertTrue(TokenUtil.parse(tampered, now) <= 0);
        assertTrue(TokenUtil.parse(token, now + (TokenUtil.REMEMBER_DAYS + 1) * 24L * 3600) <= 0);
        assertTrue(TokenUtil.parse("garbage", now) <= 0);
        assertTrue(TokenUtil.parse(null, now) <= 0);
    }

    @Test
    void jsonEscapes() {
        assertEquals("{\"ok\":true,\"message\":\"a \\\"b\\\"\"}", Json.obj("ok", true, "message", "a \"b\""));
    }
}
