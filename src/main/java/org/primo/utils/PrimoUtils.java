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

    // NOTE: "Include functionality to determine if the generated
    // number is prime and handle game outcomes based on this."
    public static boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    public static String generateRandomSeed() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return bytesToHex(bytes);
    }

    // NOTE: "Implement logic to generate a random number between 1 and 20
    // for each game spin which is cryptographically secure (server seed, client seed, and nonce)
    // for which the results can be decoded using this information after they are generated.
    // Results should be reproducible using (server seed, client seed and nonce)."
    @SneakyThrows
    public static int generateSecureNumber(String serverSeed, String clientSeed, int nonce) {
        String hash = hashSHA256(serverSeed + clientSeed + nonce);
        return (int) (Long.parseLong(hash.substring(0, 8), 16) % 20) + 1;
    }

    public static int generateRandomNonce() {
        return new SecureRandom().nextInt(1000);
    }

    public static String createShortUUIDReference(UUID reference) {
        return Arrays.stream(reference.toString().split("-"))
                .map(part -> part.substring(0, Math.min(3, part.length())).toUpperCase())
                .collect(Collectors.joining());
    }

    public static String generateSpinToken() {
        return createShortUUIDReference(UUID.randomUUID());
    }
    private static String bytesToHex(byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format("%02x", bytes[i]))
                .collect(Collectors.joining());
    }

    @SneakyThrows
    private static String hashSHA256(String input) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

}
