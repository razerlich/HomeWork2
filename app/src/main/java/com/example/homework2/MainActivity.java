package com.example.homework2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bumptech.glide.Glide;
import com.example.homework2.room.database.UserDatabase;
import com.example.homework2.room.entity.UserEntity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RandomUserApi api;
    private UserDatabase db;
    private User currentUser;
    private boolean isLoading = false;

    private TextView firstNameTextView, lastNameTextView, emailTextView, ageTextView, cityTextView, countryTextView;
    private ImageView userImageView;
    private Button btnNextUser, btnAddUser, btnViewCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = RandomUserApiClient.getClient().create(RandomUserApi.class);
        db = UserDatabase.getInstance(this);

        initializeViews();
        setupListeners();
        fetchRandomUser();
    }

    private void initializeViews() {
        firstNameTextView = findViewById(R.id.textViewFirstName);
        lastNameTextView = findViewById(R.id.textViewLastName);
        emailTextView = findViewById(R.id.textViewEmail);
        ageTextView = findViewById(R.id.textViewAge);
        cityTextView = findViewById(R.id.textViewCity);
        countryTextView = findViewById(R.id.textViewCountry);
        userImageView = findViewById(R.id.imageViewUser);

        btnNextUser = findViewById(R.id.buttonNextUser);
        btnAddUser = findViewById(R.id.buttonAddUser);
        btnViewCollection = findViewById(R.id.buttonViewCollection);
    }

    private void setupListeners() {
        btnNextUser.setOnClickListener(v -> fetchRandomUser());
        btnAddUser.setOnClickListener(v -> addCurrentUserToCollection());
        btnViewCollection.setOnClickListener(v -> viewUserCollection());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRandomUser();
    }
    private void fetchRandomUser() {
        setLoadingState(true);
        api.getRandomUser().enqueue(new Callback<RandomUserResponse>() {
            @Override
            public void onResponse(Call<RandomUserResponse> call, Response<RandomUserResponse> response) {
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<RandomUserResponse.Result> results = response.body().getResults();
                    if (results != null && !results.isEmpty()) {
                        RandomUserResponse.Result result = results.get(0);
                        String userId = result.getId().getValue();
                        Log.d("USER_ID", "User ID: " + userId);  // Add this line
                        if (userId == null || userId.isEmpty()) {
                            Log.e("USER_ID", "User ID is null or empty");
                            Toast.makeText(MainActivity.this, "Invalid user data received", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        currentUser = new User(
                                userId,
                                result.getName().getFirst(),
                                result.getName().getLast(),
                                result.getDob().getAge(),
                                result.getEmail(),
                                result.getLocation().getCity(),
                                result.getLocation().getCountry(),
                                result.getPicture().getLarge()
                        );
                        updateUI(currentUser);
                    } else {
                        showError("No user data received");
                        currentUser = null;
                    }
                } else {
                    showError("Error: " + response.code());
                    currentUser = null;
                }
            }

            @Override
            public void onFailure(Call<RandomUserResponse> call, Throwable t) {
                setLoadingState(false);
                showError("Network error: " + t.getMessage());
                currentUser = null;
            }
        });
    }
//    private User convertResultToUser(RandomUserResponse.Result result) {
//        return new User(
//                result.getId().getValue(),
//                result.getName().getFirst(),
//                result.getName().getLast(),
//                result.getDob().getAge(),
//                result.getEmail(),
//                result.getLocation().getCity(),
//                result.getLocation().getCountry(),
//                result.getPicture().getLarge()
//        );
//    }

    private void updateUI(User user) {
        runOnUiThread(() -> {
            TextView firstNameTextView = findViewById(R.id.textViewFirstName);
            TextView lastNameTextView = findViewById(R.id.textViewLastName);
            TextView emailTextView = findViewById(R.id.textViewEmail);
            TextView ageTextView = findViewById(R.id.textViewAge);
            TextView cityTextView = findViewById(R.id.textViewCity);
            TextView countryTextView = findViewById(R.id.textViewCountry);
            ImageView userImageView = findViewById(R.id.imageViewUser);

            firstNameTextView.setText(user.getFirstName());
            lastNameTextView.setText(user.getLastName());
            emailTextView.setText(user.getEmail());
            ageTextView.setText(String.valueOf(user.getAge()));
            cityTextView.setText(user.getCity());
            countryTextView.setText(user.getCountry());

            Glide.with(this)
                    .load(user.getImageUrl())
                    .into(userImageView);
        });
    }
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        firstNameTextView.setText("Error");
        lastNameTextView.setText("Error");
        emailTextView.setText("Error");
        ageTextView.setText("Error");
        cityTextView.setText("Error");
        countryTextView.setText("Error");
        userImageView.setImageResource(R.drawable.errorhw2);
    }

    private void setLoadingState(boolean loading){
        isLoading = loading;
        btnAddUser.setEnabled(!loading);
        btnNextUser.setEnabled(!loading);
        btnViewCollection.setEnabled(!loading);
    }

    private void addCurrentUserToCollection() {
        if (currentUser != null) {
            new Thread(() -> {
                try {
                    UserEntity existingUser = db.userDao().getUserById(currentUser.getId());
                    if (existingUser == null) {
                        UserEntity userEntity = new UserEntity(
                                currentUser.getId(),
                                currentUser.getFirstName(),
                                currentUser.getLastName(),
                                currentUser.getAge(),
                                currentUser.getEmail(),
                                currentUser.getCity(),
                                currentUser.getCountry(),
                                currentUser.getImageUrl()
                        );
                        db.userDao().insert(userEntity);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "User added to collection", Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "User already exists in collection", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    Log.e("DATABASE", "Error saving user to database", e);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        } else {
            Toast.makeText(this, "No user to add. Please fetch a user first.", Toast.LENGTH_SHORT).show();
        }
    }
    private void viewUserCollection(){
        Intent intent = new Intent(this, UsersActivity.class);
        startActivity(intent);
    }
    private void saveUserToDatabase(User user) {
        new Thread(() -> {
            try {
                if (user.getId() == null || user.getId().isEmpty()) {
                    Log.e("DATABASE", "Attempting to save user with null or empty ID");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Invalid user data", Toast.LENGTH_SHORT).show());
                    return;
                }
                UserEntity existingUser = db.userDao().getUserById(user.getId());
                if (existingUser == null) {
                    UserEntity userEntity = new UserEntity(
                            user.getId(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getAge(),
                            user.getEmail(),
                            user.getCity(),
                            user.getCountry(),
                            user.getImageUrl()
                    );
                    db.userDao().insert(userEntity);
                    Log.d("DATABASE", "User inserted successfully: " + user.getId());
                } else {
                    Log.d("DATABASE", "User already exists: " + user.getId());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "User already exists in database", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("DATABASE", "Error saving user to database", e);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}