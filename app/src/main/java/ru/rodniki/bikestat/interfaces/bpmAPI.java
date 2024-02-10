package ru.rodniki.bikestat.interfaces;

import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Header;
import ru.rodniki.bikestat.models.bpmModel;

public interface bpmAPI {
    @GET("ppo_it/api/watch/")
    Call<bpmModel> getBpm(@Header("x-access-tokens") String token);
}
