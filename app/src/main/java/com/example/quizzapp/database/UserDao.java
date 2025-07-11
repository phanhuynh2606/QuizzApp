package com.example.quizzapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quizzapp.models.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<User> users);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM users")
    void deleteAllUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(String id);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    User getLoggedInUser();

    @Query("SELECT * FROM users WHERE (email = :loginInput) AND hashedPassword = :hashedPassword LIMIT 1")
    User getUserByLoginCredentials(String loginInput, String hashedPassword);

    @Query("UPDATE users SET isLoggedIn = 0")
    void logoutAllUsers();

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :userId")
    void setUserLoggedIn(String userId);

    @Query("SELECT * FROM users WHERE email = :email AND hashedPassword = :password LIMIT 1")
    User loginUser(String email, String password);


    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);

    // Phương thức mới để lấy user theo email để xác thực password
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserForPasswordVerification(String email);

    @Query("UPDATE users SET hashedPassword = :hashedPassword, salt = :salt WHERE id = :userId")
    void updateUserPassword(String userId, String hashedPassword, String salt);

    @Query("DELETE FROM users")
    void deleteAll();
}
