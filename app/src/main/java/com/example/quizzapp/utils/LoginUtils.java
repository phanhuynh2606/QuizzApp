package com.example.quizzapp.utils;

import java.util.regex.Pattern;

public class LoginUtils {

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    /**
     * Kiểm tra xem input có phải là email không
     * @param input Chuỗi cần kiểm tra
     * @return true nếu là email, false nếu là username
     */
    public static boolean isEmail(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(input.trim()).matches();
    }

    /**
     * Validate input cho đăng nhập
     * @param input Email hoặc username
     * @return true nếu hợp lệ
     */
    public static boolean isValidLoginInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        String trimmedInput = input.trim();

        // Nếu là email, validate email format
        if (isEmail(trimmedInput)) {
            return true;
        }

        // Nếu là username, kiểm tra độ dài và ký tự hợp lệ
        return isValidUsername(trimmedInput);
    }

    /**
     * Validate username format
     * @param username Username cần validate
     * @return true nếu username hợp lệ
     */
    private static boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 20) {
            return false;
        }

        // Username chỉ chứa chữ cái, số và dấu gạch dưới
        return Pattern.matches("^[a-zA-Z0-9_]+$", username);
    }

    /**
     * Validate password
     * @param password Password cần validate
     * @return true nếu password hợp lệ
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
