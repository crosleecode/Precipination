package com.example.precipination

import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class NotificationViewModelTest {

    private lateinit var dispatcher: TestDispatcher
    private lateinit var service: MockPrecipinationService
    private lateinit var viewModel: NotificationViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        dispatcher = UnconfinedTestDispatcher()
        service = MockPrecipinationService()
        viewModel = NotificationViewModel(service, "key")
    }

    @Test
    fun fetchWeatherByLocation_correctWeatherInfo() = runTest {
        var result: WeatherInfo? = null
        viewModel.fetchWeatherByLocation(44.95, -93.16) {
            result = it
        }
        assertEquals("55104", result?.name)
    }

    @Test
    fun fetchWeatherByLocation_failWeatherInfo() = runTest {
        service.shouldReturnError = true
        var result: WeatherInfo? = WeatherInfo()
        viewModel.fetchWeatherByLocation(44.95, -93.16) {
            result = it
        }
        assertNull(result)
    }

}