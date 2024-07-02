package com.example.homework2.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.homework2.room.entity.UserEntity;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getUserById(String id);
}