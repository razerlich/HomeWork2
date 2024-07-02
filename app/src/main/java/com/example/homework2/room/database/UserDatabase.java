package com.example.homework2.room.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.homework2.room.dao.UserDao;
import com.example.homework2.room.entity.UserEntity;

@Database(entities = {UserEntity.class}, version = 2)
public abstract class UserDatabase extends RoomDatabase {
    private static volatile UserDatabase INSTANCE;

    public abstract UserDao userDao();

    public static UserDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    UserDatabase.class, "user_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}