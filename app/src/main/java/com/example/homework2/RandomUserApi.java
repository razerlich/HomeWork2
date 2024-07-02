package com.example.homework2;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RandomUserApi {
    @GET("api")
    Call<RandomUserResponse> getRandomUser();
}
