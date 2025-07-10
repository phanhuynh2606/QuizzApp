package com.example.quizzapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    private static final String BASE_URL = "https://your-api-base-url.com/api/"; // Thay đổi URL này
    private static Retrofit retrofit;
    private static QuizApiService apiService;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static QuizApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofitInstance().create(QuizApiService.class);
        }
        return apiService;
    }
}
