package com.parking.auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private PasswordUtil() {}

    public static String hash(String password) {
        try {
            byte[] salt = new byte[16]; RANDOM.nextBytes(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120000, 256);
            byte[] key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(key);
        } catch (Exception e) { throw new IllegalStateException("Password hashing failed", e); }
    }

    public static boolean matches(String password, String stored) {
        try {
            String[] parts = stored.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expected = Base64.getDecoder().decode(parts[1]);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120000, expected.length * 8);
            byte[] actual = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            spec.clearPassword();
            if (actual.length != expected.length) return false;
            int diff = 0; for (int i = 0; i < actual.length; i++) diff |= actual[i] ^ expected[i];
            return diff == 0;
        } catch (Exception e) { return false; }
    }
}
