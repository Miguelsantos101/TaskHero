package com.example.taskhero;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.taskhero.util.HashUtils;
import org.junit.Test;

/**
 * Unit test for the HashUtils class.
 * Verifies that SHA-256 hash generation is correct.
 */
public class HashUtilsTest {

    @Test
    public void sha256_generatesCorrectHash() {
        String input = "password123";
        String expectedHash = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f";

        String generatedHash = HashUtils.sha256(input);

        assertNotNull(generatedHash);
        assertEquals(expectedHash, generatedHash.toLowerCase());
    }

    @Test
    public void sha256_handlesEmptyString() {
        String input = "";
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        String generatedHash = HashUtils.sha256(input);

        assertNotNull(generatedHash);
        assertEquals(expectedHash, generatedHash.toLowerCase());
    }
}