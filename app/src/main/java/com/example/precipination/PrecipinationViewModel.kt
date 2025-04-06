package com.example.precipination

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PrecipinationViewModel(

    private val precipinationService : PrecipinationService,
    private val apiKey : String,

) : ViewModel() {

    private val _weatherInfo : MutableLiveData<WeatherInfo> = MutableLiveData()
    val weatherInfo : LiveData<WeatherInfo> = _weatherInfo

    fun fetchWeatherData(zipCode : String) {
        val call = precipinationService.getCurrentWeather(zipCode, apiKey)
        call.enqueue(object : Callback<WeatherInfo> {
            override fun onResponse(p0: Call<WeatherInfo>, p1: Response<WeatherInfo>) {
                if (p1.isSuccessful) {
                    _weatherInfo.value = p1.body()
                } else {
                    Log.e("Weather", "Failure fetching weather data: ${p1.code()}")
                }
            }

            override fun onFailure(p0: Call<WeatherInfo>, p1: Throwable) {
                Log.e("Weather", "Failure Fetching Weather", p1)
            }
        })
    }



}