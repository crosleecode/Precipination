package com.example.precipination

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastInfo(
    val city: CityInfo,
    val cnt: Int,
    val list: List<WeatherForecast>
)

@Serializable
data class CityInfo(
    val name: String,
    val country: String,
    val coord: Coordinates
)

@Serializable
data class WeatherForecast(
    val dt: Long,
    val temp: TempForecast,
    val weather: List<ConditionsForecast>
){
    val date: String
        get() = java.text.SimpleDateFormat("MM/dd", java.util.Locale.getDefault())
            .format(java.util.Date(dt * 1000))
}

@Serializable
data class TempForecast(
    val day: Double,
    val min: Double,
    val max: Double
)

@Serializable
data class ConditionsForecast(
    val icon: String = "",
    val main: String = "",
    val description: String = ""
)


