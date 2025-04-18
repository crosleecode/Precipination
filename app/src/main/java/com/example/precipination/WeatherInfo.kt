package com.example.precipination

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherInfo(
    val main: WeatherData= WeatherData(),
    val name: String = "",
    val weather: List<WeatherCondition> = emptyList(),
    val coord: Coordinates = Coordinates()
)

@Serializable
data class WeatherCondition(
    val icon: String = "",
    val main: String = "",
    val description: String = ""
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

    @SerialName("pressure")
    val pressure: Int = 0,

    @SerialName("humidity")
    val humidity: Int = 0
)

@Serializable
data class Coordinates(
    val lon: Double = 0.0,
    val lat: Double = 0.0
)