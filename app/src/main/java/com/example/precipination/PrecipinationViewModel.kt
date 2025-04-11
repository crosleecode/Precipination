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

    private val _alert = MutableLiveData<String?>()
    val alert : LiveData<String?> = _alert

    fun fetchWeatherData(zipCode : String) {
        val call = precipinationService.getCurrentWeather(zipCode, apiKey)
        call.enqueue(object : Callback<WeatherInfo> {
            override fun onResponse(p0: Call<WeatherInfo>, p1: Response<WeatherInfo>) {
                if (p1.isSuccessful) {
                    _weatherInfo.value = p1.body()
                    _alert.value = null
                } else {
                    _alert.value = "Invalid Zip Code"
                    Log.e("Weather", "Failure fetching weather data: ${p1.code()}")
                }
            }

            override fun onFailure(p0: Call<WeatherInfo>, p1: Throwable) {
                Log.e("Weather", "Failure Fetching Weather", p1)
            }
        })
    }

    fun clearAlert() {
        _alert.value = null
    }


}