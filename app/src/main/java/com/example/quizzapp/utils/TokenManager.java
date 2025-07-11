package com.example.quizzapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class để quản lý JWT tokens (access token, refresh token)
 */
public class TokenManager {
    private static final String PREF_NAME = "auth_tokens";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_TOKEN_TIMESTAMP = "token_timestamp";

    private SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lưu tokens sau khi đăng nhập/đăng ký thành công
     */
    public void saveTokens(String accessToken, String refreshToken, String tokenType, String expiresIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_TOKEN_TYPE, tokenType);
        editor.putString(KEY_EXPIRES_IN, expiresIn);
        editor.putLong(KEY_TOKEN_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Lấy access token
     */
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    /**
     * Lấy refresh token
     */
    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    /**
     * Lấy token type (thường là "Bearer")
     */
    public String getTokenType() {
        return sharedPreferences.getString(KEY_TOKEN_TYPE, "Bearer");
    }

    /**
     * Kiểm tra xem có token không
     */
    public boolean hasToken() {
        return getAccessToken() != null;
    }

    /**
     * Kiểm tra token có hết hạn không (dựa trên expiresIn)
     */
    public boolean isTokenExpired() {
        String expiresIn = sharedPreferences.getString(KEY_EXPIRES_IN, "15m");
        long timestamp = sharedPreferences.getLong(KEY_TOKEN_TIMESTAMP, 0);

        if (timestamp == 0) return true;

        // Chuyển đổi expiresIn (ví dụ: "15m") thành milliseconds
        long expirationTime = parseExpirationTime(expiresIn);
        return (System.currentTimeMillis() - timestamp) > expirationTime;
    }

    /**
     * Xóa tất cả tokens (khi đăng xuất)
     */
    public void clearTokens() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Lấy Authorization header để sử dụng trong API calls
     */
    public String getAuthorizationHeader() {
        String accessToken = getAccessToken();
        String tokenType = getTokenType();

        if (accessToken != null) {
            return tokenType + " " + accessToken;
        }
        return null;
    }

    /**
     * Chuyển đổi expiresIn string thành milliseconds
     */
    private long parseExpirationTime(String expiresIn) {
        if (expiresIn == null || expiresIn.isEmpty()) {
            return 15 * 60 * 1000; // Mặc định 15 phút
        }

        try {
            if (expiresIn.endsWith("m")) {
                int minutes = Integer.parseInt(expiresIn.substring(0, expiresIn.length() - 1));
                return minutes * 60 * 1000;
            } else if (expiresIn.endsWith("h")) {
                int hours = Integer.parseInt(expiresIn.substring(0, expiresIn.length() - 1));
                return hours * 60 * 60 * 1000;
            } else if (expiresIn.endsWith("d")) {
                int days = Integer.parseInt(expiresIn.substring(0, expiresIn.length() - 1));
                return days * 24 * 60 * 60 * 1000;
            } else {
                // Nếu chỉ là số, coi như là giây
                int seconds = Integer.parseInt(expiresIn);
                return seconds * 1000;
            }
        } catch (NumberFormatException e) {
            return 15 * 60 * 1000; // Mặc định 15 phút nếu parse lỗi
        }
    }
}
