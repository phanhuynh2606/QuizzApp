package com.example.quizzapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.quizzapp.models.User;

@Database(
 entities ={User.class},
version = 1,
exportSchema = true

)
public abstract class QuizDatabase  extends RoomDatabase {

    private static final String DATABASE_NAME = "quiz_database";
    private static QuizDatabase instance;

    public abstract UserDao userDao();

    public abstract  QuizDao quizDao();
    public static synchronized QuizDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            QuizDatabase.class,
                            DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


}
