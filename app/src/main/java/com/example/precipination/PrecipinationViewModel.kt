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
    private val apiKey : String

) : ViewModel() {
    private val _weatherInfo : MutableLiveData<WeatherInfo> = MutableLiveData()
    val weatherInfo : LiveData<WeatherInfo> = _weatherInfo

    private val _alert = MutableLiveData<String?>()
    val alert : LiveData<String?> = _alert

    private val _forecastInfo = MutableLiveData<List<WeatherForecast>>()
    val forecastInfo: LiveData<List<WeatherForecast>> = _forecastInfo

    private var coordinates: Coordinates? = null

    fun fetchWeatherData(zipCode : String) {
        val call = precipinationService.getCurrentWeather(zipCode, apiKey)
        call.enqueue(object : Callback<WeatherInfo> {
            override fun onResponse(p0: Call<WeatherInfo>, p1: Response<WeatherInfo>) {
                if (p1.isSuccessful) {
                    p1.body()?.let { data ->
                        _weatherInfo.value = data
                        _alert.value = null
                        coordinates = data.coord
                    }
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

    fun fetchForecast() {
        val coords = weatherInfo.value?.coord ?: return
        val call = precipinationService.getForecast(lat = coords.lat, lon = coords.lon, cnt = 16, apiKey = apiKey)
        call.enqueue(object : Callback<ForecastInfo> {
            override fun onResponse(p0: Call<ForecastInfo>, p1: Response<ForecastInfo>) {
                if (p1.isSuccessful) {
                    _forecastInfo.value = p1.body()?.list
                } else {
                    Log.e("Forecast", "Response failed: ${p1.code()}")
                }
            }

            override fun onFailure(call: Call<ForecastInfo>, t: Throwable) {
                Log.e("Forecast", "Failure Fetching Weather Forecast", t)
            }
        })
    }

    fun clearAlert() {
        _alert.value = null
    }


}