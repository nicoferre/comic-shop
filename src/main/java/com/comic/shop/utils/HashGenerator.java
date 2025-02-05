package com.comic.shop.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Component
public class HashGenerator {

    @Value("${marvel.public.key}")
    protected String publicKey;

    @Value("${marvel.private.key}")
    protected String privateKey;

    public String generateMD5Hash(String input) {
        Objects.requireNonNull(input, "Input cannot be null");
        Objects.requireNonNull(publicKey, "Public key cannot be null");
        Objects.requireNonNull(privateKey, "Private key cannot be null");

        try {
            String hashInput = new StringBuilder(input)
                    .append(privateKey)
                    .append(publicKey)
                    .toString();

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(hashInput.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No MD5 algorithm found", e);
        }
    }
}