package com.example.quizzapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quizzapp.R;
import com.example.quizzapp.models.User;
import com.example.quizzapp.repository.AuthRepository;
import com.example.quizzapp.repository.QuizRepository;

public class DashboardActivity extends AppCompatActivity {
    private TextView tvWelcome, tvTotalQuizzes, tvAverageScore;
    private Button btnTakeQuiz, btnViewHistory, btnSyncData;
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
        tvTotalQuizzes = findViewById(R.id.tvTotalQuizzes);
        tvAverageScore = findViewById(R.id.tvAverageScore);
        btnTakeQuiz = findViewById(R.id.btnTakeQuiz);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnSyncData = findViewById(R.id.btnSyncData);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard");
        }
    }

    private void loadUserData() {
        authRepository.getCurrentUser(new AuthRepository.UserCallback() {
            @Override
            public void onResult(User user) {
                if (user != null) {
                    currentUser = user;
                    runOnUiThread(() -> {
                        tvWelcome.setText("Welcome, " + user.getFullName());
                        loadStatistics();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(DashboardActivity.this, "No user logged in", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    });
                }
            }
        });
    }

    private void loadStatistics() {
        if (currentUser == null) return;

        // Load user statistics from database
        new Thread(() -> {
            // This would be implemented in repository to get user stats
            runOnUiThread(() -> {
                tvTotalQuizzes.setText("Total Quizzes: Loading...");
                tvAverageScore.setText("Average Score: Loading...");
            });
        }).start();
    }

    private void setupClickListeners() {
        btnTakeQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SemesterActivity.class);
            startActivity(intent);
        });
        }
//
//        btnViewHistory.setOnClickListener(v -> {
//            Intent intent = new Intent(DashboardActivity.this, HistoryActivity.class);
//            startActivity(intent);
//        });
//
//        btnSyncData.setOnClickListener(v -> {
//            // Implement sync functionality
//            Toast.makeText(this, "Syncing data...", Toast.LENGTH_SHORT).show();
//        });
//    }

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

}
