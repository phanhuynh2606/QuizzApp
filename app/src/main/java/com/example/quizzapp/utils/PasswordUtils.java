package com.example.quizzapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class để hash và xác thực password
 */
public class PasswordUtils {

    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Hash password với salt để lưu trữ an toàn
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());

            // Chuyển đổi byte array thành string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Tạo salt ngẫu nhiên
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash password với salt được tạo tự động
     */
    public static HashedPassword hashPasswordWithSalt(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return new HashedPassword(hashedPassword, salt);
    }

    /**
     * Xác thực password
     */
    public static boolean verifyPassword(String password, String hashedPassword, String salt) {
        String hashToCheck = hashPassword(password, salt);
        return hashToCheck.equals(hashedPassword);
    }

    /**
     * Class để lưu trữ password đã hash và salt
     */
    public static class HashedPassword {
        private final String hashedPassword;
        private final String salt;

        public HashedPassword(String hashedPassword, String salt) {
            this.hashedPassword = hashedPassword;
            this.salt = salt;
        }

        public String getHashedPassword() {
            return hashedPassword;
        }

        public String getSalt() {
            return salt;
        }
    }
}
