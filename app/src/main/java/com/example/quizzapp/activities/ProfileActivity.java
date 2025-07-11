package com.example.quizzapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quizzapp.R;
import com.example.quizzapp.models.User;
import com.example.quizzapp.repository.AuthRepository;
import com.example.quizzapp.utils.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    // UI Components
    private TextView tvEmail, tvFullName, tvRole, tvDeviceType, tvCreatedAt, tvLastLogin;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword, btnLogout;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    // Repository and Utils
    private AuthRepository authRepository;
    private TokenManager tokenManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        initRepository();
        setupToolbar();
        setupClickListeners();
        loadUserProfile();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);

        // User info views
        tvEmail = findViewById(R.id.tvEmail);
        tvFullName = findViewById(R.id.tvFullName);
        tvRole = findViewById(R.id.tvRole);
        tvDeviceType = findViewById(R.id.tvDeviceType);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvLastLogin = findViewById(R.id.tvLastLogin);

        // Password change views
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        // Buttons
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        progressBar = findViewById(R.id.progressBar);
    }

    private void initRepository() {
        authRepository = new AuthRepository(this);
        tokenManager = new TokenManager(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }
    }

    private void setupClickListeners() {
        toolbar.setNavigationOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> handleChangePassword());

        btnLogout.setOnClickListener(v -> handleLogout());
    }

    private void loadUserProfile() {
        showLoading(true);

        authRepository.getCurrentUser(user -> {
            runOnUiThread(() -> {
                showLoading(false);
                if (user != null) {
                    currentUser = user;
                    displayUserInfo(user);
                } else {
                    Toast.makeText(this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void displayUserInfo(User user) {
        tvEmail.setText(user.getEmail());
        tvFullName.setText(user.getFullName());
        tvRole.setText(capitalizeFirst(user.getRole()));
        tvDeviceType.setText(capitalizeFirst(user.getDeviceType()));

        // Format và hiển thị dates
        if (user.getServerCreatedAt() != null) {
            tvCreatedAt.setText(formatDate(user.getServerCreatedAt()));
        } else {
            tvCreatedAt.setText("N/A");
        }

        if (user.getLastLogin() != null) {
            tvLastLogin.setText(formatDate(user.getLastLogin()));
        } else {
            tvLastLogin.setText("N/A");
        }
    }

    private String formatDate(String isoDateString) {
        try {
            // Parse ISO date từ server (format: 2025-07-11T11:47:58.993Z)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            Date date = inputFormat.parse(isoDateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            return isoDateString; // Fallback to original string
        }
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return "N/A";
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private void handleChangePassword() {
        String currentPassword = etCurrentPassword.getText() != null ?
            etCurrentPassword.getText().toString().trim() : "";
        String newPassword = etNewPassword.getText() != null ?
            etNewPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ?
            etConfirmPassword.getText().toString().trim() : "";

        // Validate input
        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Please enter current password");
            etCurrentPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Please enter new password");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            etNewPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // TODO: Implement change password API call
        showLoading(true);

        // For now, just show success message
        // In a real implementation, you would call an API to change password
        showLoading(false);
        Toast.makeText(this, "Password change feature will be implemented with API", Toast.LENGTH_LONG).show();

        // Clear password fields
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
    }

    private void handleLogout() {
        showLoading(true);

        authRepository.logout(new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    showLoading(false);

                    // Clear tokens
                    tokenManager.clearTokens();

                    Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                    // Navigate to LoginActivity
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(ProfileActivity.this, "Logout failed: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnChangePassword.setEnabled(!show);
        btnLogout.setEnabled(!show);
        etCurrentPassword.setEnabled(!show);
        etNewPassword.setEnabled(!show);
        etConfirmPassword.setEnabled(!show);
    }
}
