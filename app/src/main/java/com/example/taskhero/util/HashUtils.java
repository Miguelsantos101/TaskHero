package com.example.taskhero.util;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for cryptographic functions.
 * Contains methods for generating password hashes.
 */
public final class HashUtils {

    private static final String TAG = "HashUtils";

    private HashUtils() {
    }

    /**
     * Generates a SHA-256 hash for the input string.
     * Used to securely store passwords.
     *
     * @param base The string to be hashed (the user's password).
     * @return The hexadecimal representation of the SHA-256 hash, or null in case of an error.
     */
    public static String sha256(final String base) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error generating SHA-256 hash", e);
            return null;
        }
    }
}