package com.example.quizzapp.api;

import com.example.quizzapp.models.User;
import com.example.quizzapp.models.api.ApiResponse;
import com.example.quizzapp.models.api.ChangePasswordRequest;
import com.example.quizzapp.models.api.LoginRequest;
import com.example.quizzapp.models.api.LoginResponse;
import com.example.quizzapp.models.api.ProfileResponse;
import com.example.quizzapp.models.api.RegisterRequest;
import com.example.quizzapp.models.api.RegisterResponse;
import com.example.quizzapp.models.api.RefreshTokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * API Service chuyên xử lý Authentication
 */
public interface AuthApiService {

    @GET("auth/profile")
    Call<ApiResponse<ProfileResponse>> getProfile();

    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<ApiResponse<RegisterResponse>> register(@Body RegisterRequest registerRequest);

    @PUT("auth/change-password")
    Call<ApiResponse<Void>> changePassword(
        @Body ChangePasswordRequest changePasswordRequest
    );

    @POST("auth/refresh-token")
    Call<ApiResponse<LoginResponse>> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout();
}
