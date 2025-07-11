package com.example.quizzapp.api;

import com.example.quizzapp.models.User;
import com.example.quizzapp.models.api.ApiResponse;
import com.example.quizzapp.models.api.LoginRequest;
import com.example.quizzapp.models.api.LoginResponse;
import com.example.quizzapp.models.api.RegisterRequest;
import com.example.quizzapp.models.api.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * API Service chuyên xử lý Authentication
 */
public interface AuthApiService {

    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<ApiResponse<RegisterResponse>> register(@Body RegisterRequest registerRequest);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout();
}
