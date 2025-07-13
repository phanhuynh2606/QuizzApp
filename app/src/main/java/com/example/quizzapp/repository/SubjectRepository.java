package com.example.quizzapp.repository;

import android.content.Context;
import android.util.Log;

import com.example.quizzapp.api.ApiClient;
import com.example.quizzapp.api.SubjectApiService;
import com.example.quizzapp.models.Subject;
import com.example.quizzapp.models.api.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectRepository {
    private static final String TAG = "SubjectRepository";
    private final SubjectApiService apiService;

    public SubjectRepository(Context context) {
        this.apiService = ApiClient.getSubjectService(context);
    }

    public void getSubjectsBySemesterId(String semesterId, SubjectCallback callback) {
        apiService.getAllSubjects(semesterId).enqueue(new Callback<ApiResponse<List<Subject>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Subject>>> call, Response<ApiResponse<List<Subject>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Subject> subjects = response.body().getData();
                    Log.d(TAG, "Fetched " + subjects.size() + " subjects for semester " + semesterId);
                    callback.onSuccess(subjects);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load subjects";
                    Log.w(TAG, "Error fetching subjects: " + message);
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Subject>>> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public interface SubjectCallback {
        void onSuccess(List<Subject> subjects);
        void onError(String message);
    }
}
