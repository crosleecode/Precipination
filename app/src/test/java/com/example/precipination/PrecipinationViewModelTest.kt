package com.example.precipination

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals


class PrecipinationViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dispatcher: TestDispatcher
    private lateinit var service: MockPrecipinationService
    private lateinit var viewModel: PrecipinationViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        dispatcher = UnconfinedTestDispatcher()
        service = MockPrecipinationService()
        viewModel = PrecipinationViewModel(service, "key", dispatcher)
    }

    @Test
    fun fetchWeatherData_correctZip() = runTest {
        viewModel.fetchWeatherData("55104")
        val result = viewModel.weatherInfo.value
        assertEquals("55104", result?.name)
    }

    @Test
    fun fetchForecast_setsForecastInfo() = runTest {
        service.testWeatherInfo = WeatherInfo(name = "55104", coord = Coordinates(44.95, -93.16))

        viewModel.fetchWeatherData("55104")
        viewModel.fetchForecast()

        val forecast = viewModel.forecastInfo.value
        assertEquals(16, forecast?.size)
    }

    @Test
    fun fetchCurrentLocationWeather_setsWeatherInfo() = runTest {
        viewModel.fetchCurrentLocationWeather(44.95, -93.16)
        val result = viewModel.weatherInfo.value
        assertEquals("55104", result?.name)
    }

    @Test
    fun fetchCurrentLocationWeather_setsNullOnFailure() = runTest {
        service.shouldReturnError = true
        viewModel.fetchCurrentLocationWeather(44.95, -93.16)
        val result = viewModel.weatherInfo.value
        assertNull(result)
    }

}