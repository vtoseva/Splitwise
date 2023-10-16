package bg.sofia.uni.fmi.mjt.splitwise.utility;

import bg.sofia.uni.fmi.mjt.splitwise.exception.InternalErrorException;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// PBKDF2 algorithm
public class PasswordHasher {
    private static final int SALT_LENGTH = 16; // in bytes
    private static final int KEY_LENGTH = 256; // in bits
    private static final int ITERATIONS = 10000; // number of iterations
    private static final int BYTES = 4;

    private PasswordHasher() {
    }

    public static String hashPassword(String password) throws InternalErrorException {
        byte[] salt = generateSalt();
        byte[] hashedPassword = hashPassword(password, salt);
        return toHex(salt) + ":" + toHex(hashedPassword);
    }

    public static boolean verifyPassword(String password, String hash) throws InternalErrorException {
        String[] parts = hash.split(":");
        byte[] salt = fromHex(parts[0]);
        byte[] hashedPassword = fromHex(parts[1]);
        byte[] testHash = hashPassword(password, salt);
        return slowEquals(hashedPassword, testHash);
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private static byte[] hashPassword(String password, byte[] salt) throws InternalErrorException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InternalErrorException(e);
        }
    }

    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), SALT_LENGTH) << BYTES)
                + Character.digit(hex.charAt(i + 1), SALT_LENGTH));
        }
        return data;
    }

    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

}