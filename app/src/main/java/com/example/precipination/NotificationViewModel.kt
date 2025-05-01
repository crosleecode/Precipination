package com.example.precipination

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationViewModel (
    private val precipinationService: PrecipinationService,
    private val apiKey: String
) : ViewModel(){

    fun fetchWeatherByLocation(lat: Double, lon: Double, onResult: (WeatherInfo?) -> Unit){
        val call = precipinationService.getCurrentLocationWeather(lat, lon, apiKey)

        call.enqueue(object : Callback<WeatherInfo> {
            override fun onResponse(p0: Call<WeatherInfo>, p1: Response<WeatherInfo>){
                if(p1.isSuccessful){
                    p1.body()?.let { data ->
                        onResult(data)
                    }
                }else {
                    //Log.e("Weather", "Failure fetching weather data: ${p1.code()}")
                }
            }

            override fun onFailure(p0: Call<WeatherInfo>, p1: Throwable) {
                //Log.e("Weather", "Failure Fetching Weather", p1)
                onResult(null)
            }

        })
    }


}
