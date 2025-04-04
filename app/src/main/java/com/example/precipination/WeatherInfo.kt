package com.example.precipination

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherInfo(
    val main: WeatherData= WeatherData()
)

@Serializable
data class WeatherData(
    val temp: Double = 0.0,

    @SerialName("feels_like")
    val feelsLike: Double = 0.0,

    @SerialName("temp_min")
    val tempMin: Double = 0.0,

    @SerialName("temp_max")
    val tempMax: Double = 0.0,

    val pressure: Int = 0,

    val humidity: Int = 0
)