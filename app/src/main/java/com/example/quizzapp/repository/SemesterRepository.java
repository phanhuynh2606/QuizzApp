package com.example.quizzapp.repository;

import android.content.Context;
import android.util.Log;

import com.example.quizzapp.api.SemesterApiService;
import com.example.quizzapp.models.Semester;
import com.example.quizzapp.models.api.ApiResponse;
import com.example.quizzapp.api.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SemesterRepository {
    private static final String TAG = "SemesterRepository";

    private final SemesterApiService apiService;


    public SemesterRepository(Context context) {
        this.apiService = ApiClient.getSemesterService(context);
    }


    public void getAllSemesters(SemesterCallback callback) {
        apiService.getAllSemester().enqueue(new Callback<ApiResponse<List<Semester>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Semester>>> call, Response<ApiResponse<List<Semester>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Semester> semesters = response.body().getData();
                    Log.d(TAG, "Fetched " + semesters.size() + " semesters from API");
                    callback.onSuccess(semesters);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load semesters";
                    Log.w(TAG, "Error fetching semesters: " + message);
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Semester>>> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface SemesterCallback {
        void onSuccess(List<Semester> semesters);
        void onError(String message);
    }
}
