package com.cj.simplecontacts.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by chenjun on 17-8-22.
 */

public interface WeatherService {
    @GET("weather")
    Call<ResponseBody> getResult(@Query("city") String city, @Query("key") String key);
}
