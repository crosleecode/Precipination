package com.example.precipination

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PrecipinationService {

    @GET("weather")
    fun getCurrentWeather(
        @Query("zip") zipCode: String,
        @Query("appid") apiKey: String
    ): Call<WeatherInfo>
}