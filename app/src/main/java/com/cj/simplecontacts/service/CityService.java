package com.cj.simplecontacts.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by chenjun on 17-8-29.
 */

public interface CityService {
    @GET("search")
    Call<ResponseBody> getResult(@Query("city") double longitude, @Query("city") double latitude, @Query("key") String key);
}
