package com.example.quizzapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quizzapp.R;
import com.example.quizzapp.api.ApiClient;
import com.example.quizzapp.api.AuthApiService;
import com.example.quizzapp.models.User;
import com.example.quizzapp.models.api.ApiResponse;
import com.example.quizzapp.models.api.ProfileResponse;
import com.example.quizzapp.repository.AuthRepository;
import com.example.quizzapp.repository.QuizRepository;
import com.example.quizzapp.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {
    private TextView tvWelcome, tvTotalQuizzesNumber, tvAverageScoreNumber;
    private Button btnTakeQuiz, btnViewHistory;
    private AuthRepository authRepository; // Đổi thành AuthRepository cho user management
    private QuizRepository quizRepository; // Thêm QuizRepository cho quiz operations
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        authRepository = new AuthRepository(this); // Khởi tạo AuthRepository
        quizRepository = new QuizRepository(this); // Khởi tạo QuizRepository
        setupToolbar();
        loadUserData();
        setupClickListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTotalQuizzesNumber = findViewById(R.id.tvTotalQuizzesNumber);
        tvAverageScoreNumber = findViewById(R.id.tvAverageScoreNumber);
        btnTakeQuiz = findViewById(R.id.btnTakeQuiz);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        // Removed btnSyncData as it's no longer in the layout
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard");
        }
    }

    private void loadUserData() {
        // Gọi API /me để lấy thông tin user từ server
        fetchUserFromApi();

        // Fallback: load từ local database nếu API fail
        authRepository.getCurrentUser(new AuthRepository.UserCallback() {
            @Override
            public void onResult(User user) {
                if (user != null && currentUser == null) {
                    currentUser = user;
                    runOnUiThread(() -> {
                        tvWelcome.setText("Welcome, " + user.getFullName());
                        loadStatistics();
                    });
                }
            }
        });
    }

    private void fetchUserFromApi() {
        // Lấy token từ TokenManager thay vì SharedPreferences trực tiếp
        AuthApiService authApiService = ApiClient.getAuthApiService(this);

        // Không cần lấy token thủ công vì TokenInterceptor sẽ tự động thêm
        Call<ApiResponse<ProfileResponse>> call = authApiService.getProfile();

        call.enqueue(new Callback<ApiResponse<ProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileResponse>> call, Response<ApiResponse<ProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProfileResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        ProfileResponse profileResponse = apiResponse.getData();

                        // Convert ProfileResponse.user to User object
                        User userFromApi = convertProfileToUser(profileResponse.getUser());
                        currentUser = userFromApi;

                        // Lưu thông tin user vào database local
                        saveUserToLocalDatabase(userFromApi);

                        runOnUiThread(() -> {
                            updateUI(userFromApi);
                            Toast.makeText(DashboardActivity.this, "User data synced successfully", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(DashboardActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                            loadLocalUserData();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        if (response.code() == 401) {
                            // Token expired hoặc invalid - CLEAR TOKEN trước khi navigate
                            Toast.makeText(DashboardActivity.this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();

                            // Clear tokens để tránh loop vô hạn
                            clearTokensAndNavigateToLogin();
                        } else {
                            Toast.makeText(DashboardActivity.this, "Failed to fetch user data from server: " + response.code(), Toast.LENGTH_SHORT).show();
                            // Load từ local database as fallback
                            loadLocalUserData();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProfileResponse>> call, Throwable t) {
                runOnUiThread(() -> {
                    Toast.makeText(DashboardActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Load từ local database as fallback
                    loadLocalUserData();
                });
            }
        });
    }

    private User convertProfileToUser(ProfileResponse.UserProfile userProfile) {
        User user = new User();
        user.setId(userProfile.getId() != null ? userProfile.getId() : userProfile.get_id());
        user.setEmail(userProfile.getEmail());
        user.setFullName(userProfile.getFullName());
        user.setRole(userProfile.getRole());
        user.setLevel(userProfile.getLevel());
        user.setActive(userProfile.isActive());
        user.setStatus(userProfile.getStatus());
        user.setTotalQuizzes(userProfile.getTotalQuizzes());
        user.setAverageScore(userProfile.getAverageScore());
        user.setTotalStudyTime(userProfile.getTotalStudyTime());
        user.setLastLogin(userProfile.getLastLogin());
        user.setCreatedAt(userProfile.getCreatedAt());
        user.setUpdatedAt(userProfile.getUpdatedAt());
        return user;
    }

    private void updateUI(User user) {
        tvWelcome.setText("Welcome back, " + user.getFullName() + "!");
        // Cập nhật statistics từ API data
        updateStatistics(user);
    }

    private void updateStatistics(User user) {
        runOnUiThread(() -> {
            tvTotalQuizzesNumber.setText(String.valueOf(user.getTotalQuizzes()));

            if (user.getAverageScore() > 0) {
                tvAverageScoreNumber.setText(String.format("%.1f%%", user.getAverageScore()));
            } else {
                tvAverageScoreNumber.setText("0%");
            }
        });
    }

    private void saveUserToLocalDatabase(User user) {
        // Sử dụng AuthRepository để lưu user vào database
        new Thread(() -> {
            authRepository.saveUser(user, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(User savedUser) {
                    runOnUiThread(() -> {
                        // User đã được lưu thành công vào local database
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(DashboardActivity.this, "Failed to save user data locally: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }).start();
    }

    private void loadLocalUserData() {
        authRepository.getCurrentUser(new AuthRepository.UserCallback() {
            @Override
            public void onResult(User user) {
                if (user != null) {
                    currentUser = user;
                    runOnUiThread(() -> {
                        tvWelcome.setText("Welcome, " + user.getFullName() + " (Offline)");
                        updateStatistics(user); // Gọi updateStatistics thay vì loadStatistics
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(DashboardActivity.this, "No user data available", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    });
                }
            }
        });
    }

    private void loadStatistics() {
        if (currentUser == null) return;

        // Cập nhật UI với dữ liệu hiện tại thay vì hiển thị "Loading..."
        runOnUiThread(() -> {
            updateStatistics(currentUser);
        });
    }

    private void setupClickListeners() {
        btnTakeQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SemesterActivity.class);
            startActivity(intent);
        });
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, HistoryActivity.class);
            intent.putExtra("userId", currentUser.getId());
            startActivity(intent);
        });
//
//        btnSyncData.setOnClickListener(v -> {
//            // Implement sync functionality
//            Toast.makeText(this, "Syncing data...", Toast.LENGTH_SHORT).show();
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            // Navigate to Profile Activity
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            // Handle logout from menu
            handleLogoutFromMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleLogoutFromMenu() {
        authRepository.logout(new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    Toast.makeText(DashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(DashboardActivity.this, "Logout failed: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void logout() {
        authRepository.logout(new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    Toast.makeText(DashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(DashboardActivity.this, "Logout failed: " + error, Toast.LENGTH_SHORT).show();
                    // Vẫn chuyển về login dù logout failed
                    navigateToLogin();
                });
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearTokensAndNavigateToLogin() {
        // Clear tokens và logout user khỏi database local
        TokenManager tokenManager = new TokenManager(this);
        tokenManager.clearTokens();

        // Logout user khỏi database local để đảm bảo không còn session
        authRepository.logout(new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    // Navigate to login
                    navigateToLogin();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    // Vẫn navigate dù logout failed
                    navigateToLogin();
                });
            }
        });
    }

}
