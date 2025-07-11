package com.example.quizzapp.api;

import android.content.Context;
import android.util.Log;

import com.example.quizzapp.models.api.ApiResponse;
import com.example.quizzapp.models.api.LoginResponse;
import com.example.quizzapp.models.api.RefreshTokenRequest;
import com.example.quizzapp.utils.Constants;
import com.example.quizzapp.utils.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Interceptor để tự động xử lý token refresh cho tất cả API calls
 */
public class TokenInterceptor implements Interceptor {
    private static final String TAG = "TokenInterceptor";

    private final TokenManager tokenManager;
    private final Context context;
    private final Object refreshLock = new Object(); // Để tránh multiple refresh cùng lúc

    public TokenInterceptor(Context context) {
        this.context = context;
        this.tokenManager = new TokenManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Thêm Authorization header nếu có token
        Request.Builder requestBuilder = originalRequest.newBuilder();
        String authHeader = tokenManager.getAuthorizationHeader();
        if (authHeader != null) {
            requestBuilder.header("Authorization", authHeader);
        }

        Request requestWithAuth = requestBuilder.build();
        Response response = chain.proceed(requestWithAuth);

        // Kiểm tra nếu response là 401 và có refresh token
        if (response.code() == 401 && tokenManager.hasValidRefreshToken()) {
            Log.d(TAG, "Received 401, attempting token refresh");

            synchronized (refreshLock) {
                // Kiểm tra lại token (có thể đã được refresh bởi thread khác)
                String newAuthHeader = tokenManager.getAuthorizationHeader();
                if (newAuthHeader != null && !newAuthHeader.equals(authHeader)) {
                    // Token đã được refresh, thử lại request với token mới
                    response.close();
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", newAuthHeader)
                            .build();
                    return chain.proceed(newRequest);
                }

                // Thực hiện refresh token
                if (refreshToken()) {
                    Log.d(TAG, "Token refresh successful, retrying original request");
                    response.close();

                    // Retry original request với token mới
                    String refreshedAuthHeader = tokenManager.getAuthorizationHeader();
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", refreshedAuthHeader)
                            .build();
                    return chain.proceed(newRequest);
                } else {
                    Log.e(TAG, "Token refresh failed, clearing tokens");
                    tokenManager.clearTokens();
                }
            }
        }

        return response;
    }

    /**
     * Thực hiện refresh token
     * @return true nếu refresh thành công, false nếu thất bại
     */
    private boolean refreshToken() {
        try {
            String refreshTokenValue = tokenManager.getRefreshToken();
            if (refreshTokenValue == null) {
                Log.e(TAG, "No refresh token available");
                return false;
            }

            // Tạo Retrofit instance riêng cho refresh (không dùng interceptor để tránh infinite loop)
            Retrofit refreshRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AuthApiService refreshService = refreshRetrofit.create(AuthApiService.class);
            RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenValue);

            // Thực hiện sync call để refresh
            retrofit2.Response<ApiResponse<LoginResponse>> response =
                    refreshService.refreshToken(request).execute();

            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                LoginResponse loginResponse = response.body().getData();

                // Lưu tokens mới
                tokenManager.saveTokens(
                        loginResponse.getAccessToken(),
                        loginResponse.getRefreshToken(),
                        loginResponse.getTokenType(),
                        loginResponse.getExpiresIn()
                );

                Log.d(TAG, "Token refreshed successfully");
                return true;
            } else {
                Log.e(TAG, "Refresh token API call failed: " + response.code());
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error during token refresh: " + e.getMessage());
            return false;
        }
    }
}
