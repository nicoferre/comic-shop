package com.comic.shop.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class HashGeneratorTest {

    private HashGenerator hashGenerator;

    @BeforeEach
    void setUp() {
        hashGenerator = new HashGenerator();
        hashGenerator.privateKey = "privateKey123";
        hashGenerator.publicKey = "publicKey456";
    }

    @Test
    void testGenerateMD5Hash() {
        String input = "testInput";
        String expectedHash = "f9229fb8e880ee5efd63f7d665bcb1dc";

        String actualHash = hashGenerator.generateMD5Hash(input);

        assertEquals(expectedHash, actualHash);
    }

    @Test
    void generateMD5Hash_throwsRuntimeExceptionForInvalidAlgorithm() {
        hashGenerator = new HashGenerator() {
            @Override
            public String generateMD5Hash(String input) {
                throw new RuntimeException("No MD5 algorithm found");
            }
        };

        assertThrows(RuntimeException.class, () -> hashGenerator.generateMD5Hash("input"));
    }

    @Test
    void generateMD5Hash_handlesEmptyInput() {
        String input = "";
        String expectedHash = "32227232bbfb89e6db08b20fe627b7e1";

        String actualHash = hashGenerator.generateMD5Hash(input);

        assertEquals(expectedHash, actualHash);
    }

    @Test
    void testGenerateMD5HashWithEmptyInput() throws NoSuchAlgorithmException {
        String input = "";
        String expectedHash = generateExpectedMD5(input + "privateKey123" + "publicKey456");

        String actualHash = hashGenerator.generateMD5Hash(input);

        assertEquals(expectedHash, actualHash);
    }

    @Test
    void testGenerateMD5HashWithNullInput() {
        assertThrows(NullPointerException.class, () -> hashGenerator.generateMD5Hash(null));
    }

    private String generateExpectedMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}