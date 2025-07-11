package com.example.quizzapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class để quản lý JWT tokens (access token, refresh token)
 */
public class TokenManager {
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_TOKEN_TIMESTAMP = "token_timestamp";

    private SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_AUTH_TOKENS, Context.MODE_PRIVATE);
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
        return sharedPreferences.getString(KEY_TOKEN_TYPE, Constants.DEFAULT_TOKEN_TYPE);
    }

    /**
     * Kiểm tra xem có token không
     */
    public boolean hasToken() {
        return getAccessToken() != null;
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
     * Kiểm tra refresh token có hợp lệ không
     */
    public boolean hasValidRefreshToken() {
        String refreshToken = getRefreshToken();
        return refreshToken != null && !refreshToken.isEmpty();
    }
}
