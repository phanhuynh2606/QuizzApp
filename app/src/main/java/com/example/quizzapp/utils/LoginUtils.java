package com.example.quizzapp.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Utility class cho các validation liên quan đến login
 */
public class LoginUtils {

    /**
     * Kiểm tra input login có hợp lệ không (email hoặc username)
     */
    public static boolean isValidLoginInput(String input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }

        // Kiểm tra nếu là email
        if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            return true;
        }

        // Kiểm tra nếu là username (ít nhất 3 ký tự, chỉ chứa chữ và số)
        return input.length() >= 3 && input.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Kiểm tra password có hợp lệ không
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= Constants.MIN_PASSWORD_LENGTH;
    }

    /**
     * Kiểm tra email có hợp lệ không
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
