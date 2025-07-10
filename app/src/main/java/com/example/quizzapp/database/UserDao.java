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

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    User getLoggedInUser();

    @Query("SELECT * FROM users WHERE (username = :loginInput OR email = :loginInput) AND password = :password LIMIT 1")
    User getUserByLoginCredentials(String loginInput, String password);

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    List<User> getAllUsers();

    @Query("UPDATE users SET isLoggedIn = 0")
    void logoutAllUsers();

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :userId")
    void setUserLoggedIn(String userId);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User loginUser(String username, String password);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int checkUsernameExists(String username);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);
}
