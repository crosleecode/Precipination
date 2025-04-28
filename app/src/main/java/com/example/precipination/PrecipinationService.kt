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

    @GET("forecast/daily")
    fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("cnt") cnt: Int = 16,
        @Query("appid") apiKey: String
    ): Call<ForecastInfo>

    @GET("weather")
    fun getCurrentLocationWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): Call<WeatherInfo>
}