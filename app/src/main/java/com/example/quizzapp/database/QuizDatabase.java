package com.example.quizzapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.quizzapp.models.User;
import com.example.quizzapp.models.Quiz;
import com.example.quizzapp.utils.Constants;
import com.example.quizzapp.utils.Converters;

@Database(
        entities = {User.class, Quiz.class},
        version = Constants.DATABASE_VERSION,
        exportSchema = false
)
@TypeConverters({Converters.class})

public abstract class QuizDatabase extends RoomDatabase {

    private static QuizDatabase instance;

    public abstract UserDao userDao();
    public abstract QuizDao quizDao();

    public static synchronized QuizDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            QuizDatabase.class,
                            Constants.DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
