package com.example.homework2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homework2.room.database.UserDatabase;
import com.example.homework2.room.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private UserDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        setupRecyclerView();

        try {
            db = UserDatabase.getInstance(getApplicationContext());
            loadUsers();
        } catch (Exception e) {
            Log.e("UsersActivity", "Error initializing database", e);
            Toast.makeText(this, "Error initializing database", Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(new ArrayList<>());
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);
    }

    private void loadUsers() {
        new Thread(() -> {
            try {
                if (db != null && db.userDao() != null) {
                    List<UserEntity> userEntities = db.userDao().getAllUsers();
                    List<User> users = convertEntitiesToUsers(userEntities);
                    runOnUiThread(() -> {
                        userAdapter.setUsers(users);
                        userAdapter.notifyDataSetChanged();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(UsersActivity.this, "Database or UserDao is null", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e("UsersActivity", "Error loading users", e);
                runOnUiThread(() -> Toast.makeText(UsersActivity.this, "Error loading users: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private List<User> convertEntitiesToUsers(List<UserEntity> userEntities) {
        List<User> users = new ArrayList<>();
        for (UserEntity entity : userEntities) {
            users.add(new User(
                    entity.getId(),
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getAge(),
                    entity.getEmail(),
                    entity.getCity(),
                    entity.getCountry(),
                    entity.getImageUrl()
            ));
        }
        return users;
    }
}