package ru.rodniki.bikestat.interfaces;

import io.reactivex.Single;

import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.rodniki.bikestat.map.Result;

public interface ApiInterface {
    @GET("maps/api/directions/json")
    Single<Result> getDirection(@Query("mode") String mode,
                                @Query("transit_routing_preferance") String preferance,
                                @Query("origin") String origin,
                                @Query("destination") String destination,
                                @Query("key") String key);
}
