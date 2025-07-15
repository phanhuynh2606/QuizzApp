package com.example.quizzapp.utils;

/**
 * Class chứa các hằng số được sử dụng trong toàn bộ ứng dụng
 */
public class Constants {

    // API Configuration
    public static final String BASE_URL = "http://10.0.2.2:5000/api/";

    // Database Configuration
    public static final String DATABASE_NAME = "quiz_database";
    public static final int DATABASE_VERSION = 2; // Tăng từ 1 lên 2 vì đã thêm nhiều field mới vào User model

    // SharedPreferences Keys
    public static final String PREF_AUTH_TOKENS = "auth_tokens";

    // Token Configuration
    public static final String DEFAULT_TOKEN_TYPE = "Bearer";
    public static final String DEFAULT_EXPIRES_IN = "15m";

    // Validation Constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_RETRY_ATTEMPTS = 3;

    // Network Timeouts (in seconds)
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;

    // Private constructor để ngăn khởi tạo
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
