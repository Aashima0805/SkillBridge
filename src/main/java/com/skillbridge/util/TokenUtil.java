package com.skillbridge.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Signed "remember me" cookie value: userId.expiryEpochSeconds.signature
 * The signature is HMAC-SHA256, so the cookie cannot be forged or edited.
 * The password is never placed in a cookie.
 */
public final class TokenUtil {
    public static final int REMEMBER_DAYS = 7;

    private TokenUtil() {
    }

    private static String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(Config.get("app.secret", "dev-secret").getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String create(int userId, long nowEpochSeconds) {
        long expiry = nowEpochSeconds + REMEMBER_DAYS * 24L * 3600L;
        String data = userId + "." + expiry;
        return data + "." + sign(data);
    }

    /** Returns the user id if the token is genuine and not expired, otherwise -1. */
    public static int parse(String token, long nowEpochSeconds) {
        if (token == null) {
            return -1;
        }
        String[] p = token.split("\\.");
        if (p.length != 3) {
            return -1;
        }
        try {
            String expected = sign(p[0] + "." + p[1]);
            if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), p[2].getBytes(StandardCharsets.UTF_8))) {
                return -1;
            }
            if (Long.parseLong(p[1]) < nowEpochSeconds) {
                return -1;
            }
            return Integer.parseInt(p[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
