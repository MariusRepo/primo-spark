package org.primo.utils;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrimoUtils {

    public static final SecureRandom secureRandom = new SecureRandom();

    // Method to check if a number is prime
    public static boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    // Generate a random seed
    public static String generateRandomSeed() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return bytesToHex(bytes);
    }

    private static String bytesToHex(byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format("%02x", bytes[i]))
                .collect(Collectors.joining());
    }

    // Hash using SHA-256
    @SneakyThrows
    private static String hash(String input) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    // Generate a random number between 1 and 20 using the secure hash function
    @SneakyThrows
    public static int generateSecureNumber(String serverSeed, String clientSeed, int nonce) {
        String hash = hash(serverSeed + clientSeed + nonce);
        return (int) (Long.parseLong(hash.substring(0, 8), 16) % 20) + 1; // Convert hash to a number between 1 and 20
    }

    // Generate a random nonce
    public static int generateRandomNonce() {
        return secureRandom.nextInt(1000000);
    }

    // Will create a shorter UUID as a reference based on the first 3 characters of each group
    public static String createShortUUIDReference(UUID reference) {
        return Arrays.stream(reference.toString().split("-"))
                .map(part -> part.substring(0, Math.min(3, part.length())).toUpperCase())
                .collect(Collectors.joining());
    }

    public static String generateSpinToken() {
        return createShortUUIDReference(UUID.randomUUID());
    }

}
