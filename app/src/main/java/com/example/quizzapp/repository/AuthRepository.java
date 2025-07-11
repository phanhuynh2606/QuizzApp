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
import com.example.quizzapp.models.api.RegisterResponse;
import com.example.quizzapp.models.api.LoginResponse;
import com.example.quizzapp.utils.LoginUtils;
import com.example.quizzapp.utils.PasswordUtils;
import com.example.quizzapp.utils.TokenManager;

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
    private TokenManager tokenManager; // Thêm TokenManager

    public AuthRepository(Context context) {
        database = QuizDatabase.getInstance(context);
        authApiService = ApiClient.getAuthApiService();
        userDao = database.userDao();
        executor = Executors.newSingleThreadExecutor();
        tokenManager = new TokenManager(context); // Khởi tạo TokenManager
    }

    /**
     * Đăng nhập với email
     * @param loginInput Email
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

        // Log request để debug
        Log.d(TAG, "Sending login request - loginInput: " + loginInput + ", password length: " + password.length());

        // Gọi API đăng nhập
        authApiService.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                Log.d(TAG, "onResponse: "+ response.toString());
                Log.d(TAG, "Response code: " + response.code());

                // Log response body để debug
                if (response.errorBody() != null) {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Error response body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body: " + e.getMessage());
                    }
                }

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    LoginResponse loginResponse = response.body().getData();
                    User user = loginResponse.getUser();

                    // Lưu tokens vào TokenManager
                    tokenManager.saveTokens(
                        loginResponse.getAccessToken(),
                        loginResponse.getRefreshToken(),
                        loginResponse.getTokenType(),
                        loginResponse.getExpiresIn()
                    );

                    // Hash password trước khi lưu để bảo mật
                    PasswordUtils.HashedPassword hashedPassword = PasswordUtils.hashPasswordWithSalt(password);
                    user.setHashedPassword(hashedPassword.getHashedPassword());
                    user.setSalt(hashedPassword.getSalt());

                    // Lưu user vào database local
                    saveUserToLocal(user);

                    Log.d(TAG, "Login successful for user: " + user.getEmail());
                    callback.onSuccess(user);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Login failed";

                    // Thử đăng nhập offline nếu API thất bại
                    tryOfflineLogin(loginInput, password, callback, message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
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
                // Tìm user theo email
                User user = userDao.getUserForPasswordVerification(loginInput);

                if (user != null) {
                    // Xác thực password bằng cách hash password nhập vào với salt đã lưu
                    boolean isPasswordValid = PasswordUtils.verifyPassword(
                        password,
                        user.getHashedPassword(),
                        user.getSalt()
                    );

                    if (isPasswordValid) {
                        // Cập nhật trạng thái đăng nhập
                        userDao.logoutAllUsers();
                        userDao.setUserLoggedIn(user.getId());

                        Log.d(TAG, "Offline login successful for user: " + user.getEmail());
                        callback.onSuccess(user);
                    } else {
                        Log.w(TAG, "Offline login failed - invalid password");
                        callback.onError("Invalid email or password");
                    }
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
     * Đăng ký tài khoản mới
     */
    public void register(String email, String password, String fullName, AuthCallback callback) {
        RegisterRequest request = new RegisterRequest(email, password, fullName);

        authApiService.register(request).enqueue(new Callback<ApiResponse<RegisterResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<RegisterResponse>> call, Response<ApiResponse<RegisterResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    RegisterResponse registerResponse = response.body().getData();

                    // Lưu tokens vào TokenManager
                    tokenManager.saveTokens(
                        registerResponse.getAccessToken(),
                        registerResponse.getRefreshToken(),
                        registerResponse.getTokenType(),
                        registerResponse.getExpiresIn()
                    );

                    // Chuyển đổi RegisterResponse thành User object và lưu password để đăng nhập offline
                    User user = convertRegisterResponseToUser(registerResponse, email, fullName, password);

                    // Lưu user vào database local
                    saveUserToLocal(user);

                    Log.d(TAG, "Registration successful for user: " + user.getEmail());
                    callback.onSuccess(user);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Registration failed";
                    Log.w(TAG, "Registration failed: " + message);
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RegisterResponse>> call, Throwable t) {
                Log.e(TAG, "Registration API call failed: " + t.getMessage());
                callback.onError("Registration failed: " + t.getMessage());
            }
        });
    }

    /**
     * Chuyển đổi RegisterResponse thành User object
     */
    private User convertRegisterResponseToUser(RegisterResponse registerResponse, String email, String fullName, String password) {
        User user = new User();
        user.setId(registerResponse.getUser().getId());
        user.setEmail(email);
        user.setFullName(fullName);

        // Hash password trước khi lưu
        PasswordUtils.HashedPassword hashedPassword = PasswordUtils.hashPasswordWithSalt(password);
        user.setHashedPassword(hashedPassword.getHashedPassword());
        user.setSalt(hashedPassword.getSalt());

        user.setLoggedIn(true);
        user.setCreatedAt(System.currentTimeMillis());

        return user;
    }

    /**
     * Lưu thông tin user vào database local.
     * Phương thức này sẽ xóa tất cả người dùng cũ và chỉ lưu người dùng hiện tại.
     */
    private void saveUserToLocal(User user) {
        executor.execute(() -> {
            try {
                // Xóa tất cả user cũ để đảm bảo chỉ có user hiện tại trong DB
                userDao.deleteAll();

                // Đặt trạng thái đăng nhập cho user mới
                user.setLoggedIn(true);

                // Lưu user mới vào DB
                userDao.insertUser(user);

                Log.d(TAG, "User saved to local database: " + user.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error saving user to local database: " + e.getMessage());
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
        // Sử dụng TokenManager thay vì database để tránh main thread blocking
        return tokenManager.hasToken();
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
