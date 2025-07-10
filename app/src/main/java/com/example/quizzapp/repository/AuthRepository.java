package com.example.quizzapp.repository;

import android.content.Context;
import android.util.Log;

import com.example.quizzapp.api.ApiClient;
import com.example.quizzapp.api.AuthApiService;
import com.example.quizzapp.database.QuizDatabase;
import com.example.quizzapp.database.UserDao;
import com.example.quizzapp.models.User;
import com.example.quizzapp.models.api.ApiResponse;
import com.example.quizzapp.models.api.LoginRequest;
import com.example.quizzapp.models.api.RegisterRequest;
import com.example.quizzapp.utils.LoginUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository chuyên xử lý Authentication (Đăng nhập, Đăng ký, Đăng xuất)
 */
public class AuthRepository {
    private static final String TAG = "AuthRepository";

    private QuizDatabase database;
    private AuthApiService authApiService;
    private UserDao userDao;
    private ExecutorService executor;

    public AuthRepository(Context context) {
        database = QuizDatabase.getInstance(context);
        authApiService = ApiClient.getAuthApiService(); // Sử dụng method mới
        userDao = database.userDao();
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Đăng nhập với email hoặc username
     * @param loginInput Email hoặc username
     * @param password Mật khẩu
     * @param callback Callback trả kết quả
     */
    public void login(String loginInput, String password, AuthCallback callback) {
        // Validate input trước khi gọi API
        if (!LoginUtils.isValidLoginInput(loginInput)) {
            callback.onError("Please enter a valid email or username");
            return;
        }

        if (!LoginUtils.isValidPassword(password)) {
            callback.onError("Password must be at least 6 characters");
            return;
        }

        // Tạo request để gửi lên server
        LoginRequest request = new LoginRequest(loginInput, password);

        // Gọi API đăng nhập
        authApiService.login(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();

                    // Lưu user vào database local
                    saveUserToLocal(user);

                    Log.d(TAG, "Login successful for user: " + user.getUsername());
                    callback.onSuccess(user);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Login failed";

                    // Thử đăng nhập offline nếu API thất bại
                    tryOfflineLogin(loginInput, password, callback, message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Login API call failed: " + t.getMessage());

                // Thử đăng nhập offline khi không có internet
                tryOfflineLogin(loginInput, password, callback, "No internet connection");
            }
        });
    }

    /**
     * Thử đăng nhập offline từ database local
     */
    private void tryOfflineLogin(String loginInput, String password, AuthCallback callback, String apiError) {
        executor.execute(() -> {
            try {
                // Tìm user trong database local
                User user = userDao.getUserByLoginCredentials(loginInput, password);

                if (user != null) {
                    // Cập nhật trạng thái đăng nhập
                    userDao.logoutAllUsers();
                    userDao.setUserLoggedIn(user.getId());

                    Log.d(TAG, "Offline login successful for user: " + user.getUsername());
                    callback.onSuccess(user);
                } else {
                    Log.w(TAG, "Offline login failed - user not found in local database");
                    callback.onError(apiError + ". No cached credentials found.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during offline login: " + e.getMessage());
                callback.onError("Login failed: " + e.getMessage());
            }
        });
    }

    /**
     * Lưu thông tin user vào database local
     */
    private void saveUserToLocal(User user) {
        executor.execute(() -> {
            try {
                // Đăng xuất tất cả user khác
                userDao.logoutAllUsers();

                // Lưu user mới và set trạng thái đăng nhập
                userDao.insertUser(user);
                userDao.setUserLoggedIn(user.getId());

                Log.d(TAG, "User saved to local database: " + user.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error saving user to local database: " + e.getMessage());
            }
        });
    }

    /**
     * Đăng ký tài khoản mới
     */
    public void register(String username, String email, String password, String fullName, AuthCallback callback) {
        RegisterRequest request = new RegisterRequest(username, email, password, fullName);

        authApiService.register(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();

                    // Lưu user vào database local
                    saveUserToLocal(user);

                    Log.d(TAG, "Registration successful for user: " + user.getUsername());
                    callback.onSuccess(user);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Registration failed";
                    Log.w(TAG, "Registration failed: " + message);
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Registration API call failed: " + t.getMessage());
                callback.onError("Network error. Please check your connection.");
            }
        });
    }

    /**
     * Đăng xuất
     */
    public void logout(AuthCallback callback) {
        executor.execute(() -> {
            try {
                // Đăng xuất tất cả user trong database local
                userDao.logoutAllUsers();

                Log.d(TAG, "User logged out successfully");
                callback.onSuccess(null);
            } catch (Exception e) {
                Log.e(TAG, "Error during logout: " + e.getMessage());
                callback.onError("Logout failed: " + e.getMessage());
            }
        });
    }

    /**
     * Lấy user hiện tại đang đăng nhập
     */
    public void getCurrentUser(UserCallback callback) {
        executor.execute(() -> {
            try {
                User user = userDao.getLoggedInUser();
                callback.onResult(user);
            } catch (Exception e) {
                Log.e(TAG, "Error getting current user: " + e.getMessage());
                callback.onResult(null);
            }
        });
    }

    /**
     * Kiểm tra xem có user nào đang đăng nhập không
     */
    public boolean isLoggedIn() {
        try {
            User user = userDao.getLoggedInUser();
            return user != null;
        } catch (Exception e) {
            Log.e(TAG, "Error checking login status: " + e.getMessage());
            return false;
        }
    }

    // Callback interfaces
    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface UserCallback {
        void onResult(User user);
    }
}
