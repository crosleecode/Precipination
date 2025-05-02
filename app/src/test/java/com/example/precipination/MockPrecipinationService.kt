package com.example.precipination

import retrofit2.Call
import retrofit2.mock.Calls

class MockPrecipinationService: PrecipinationService {

    var shouldReturnError = false
    var testWeatherInfo = WeatherInfo(name = "55104", coord = Coordinates(44.95, -93.16))
    var testForecastInfo = ForecastInfo(
        city = CityInfo("55104", "US", Coordinates(44.94, -93.16)),
        cnt = 16,
        list = List(16) {
            WeatherForecast(
                dt = 0,
                temp = TempForecast(290.0, 290.0, 290.0),
                weather = listOf(ConditionsForecast("clear skies", "clear skies"))
            )
        }
    )

    override fun getCurrentWeather(zipCode: String, apiKey: String): Call<WeatherInfo> {
        return if (shouldReturnError) {
            Calls.failure(Exception("mock error"))
        } else {
            Calls.response(testWeatherInfo.copy(name = zipCode))
        }
    }

    override fun getForecast(lat: Double, lon: Double, cnt: Int, apiKey: String): Call<ForecastInfo> {
        return if (shouldReturnError) {
            Calls.failure(Exception("mock error"))
        } else {
            Calls.response(testForecastInfo)
        }
    }

    override fun getCurrentLocationWeather(lat: Double, lon: Double, apiKey: String): Call<WeatherInfo> {
        return if (shouldReturnError) {
            Calls.failure(Exception("mock error"))
        } else {
            Calls.response(testWeatherInfo.copy(coord = Coordinates(lat, lon)))
        }
    }

}