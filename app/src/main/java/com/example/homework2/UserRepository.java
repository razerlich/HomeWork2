package com.example.homework2;

import android.content.Context;

import com.example.homework2.room.dao.UserDao;
import com.example.homework2.room.database.UserDatabase;
import com.example.homework2.room.entity.UserEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class UserRepository {
    private UserDatabase db;
    private RandomUserApi api;
    private UserDao userDao;


    public UserRepository(Context context) {
        db = UserDatabase.getInstance(context);
        userDao = db.userDao();
        api = RandomUserApiClient.getClient().create(RandomUserApi.class);
    }

    public void fetchAndSaveRandomUser(final Callback<User> callback) {
        api.getRandomUser().enqueue(new retrofit2.Callback<RandomUserResponse>() {
            @Override
            public void onResponse(Call<RandomUserResponse> call, Response<RandomUserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RandomUserResponse.Result> results = response.body().getResults();
                    if (results != null && !results.isEmpty()) {
                        RandomUserResponse.Result result = results.get(0);
                        User user = convertResultToUser(result);
                        saveUserToDatabase(user);
                        callback.onSuccess(user);
                    } else {
                        callback.onError("No user data received");
                    }
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RandomUserResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    private User convertResultToUser(RandomUserResponse.Result result) {
        return new User(
                result.getId().getValue(),
                result.getName().getFirst(),
                result.getName().getLast(),
                result.getDob().getAge(),
                result.getEmail(),
                result.getLocation().getCity(),
                result.getLocation().getCountry(),
                result.getPicture().getLarge()
        );
    }

    private void saveUserToDatabase(User user) {
        UserEntity userEntity = convertUserToUserEntity(user);
        db.userDao().insert(userEntity);
    }

    private UserEntity convertUserToUserEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getEmail(),
                user.getCity(),
                user.getCountry(),
                user.getImageUrl()
        );
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}