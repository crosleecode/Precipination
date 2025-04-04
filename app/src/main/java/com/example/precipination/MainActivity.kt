package com.example.precipination

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.precipination.ui.theme.PrecipinationTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

//Assignment 3
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import androidx.compose.runtime.livedata.observeAsState



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val precipinationService = createRetrofitService()
        val apiKey = resources.getString(R.string.open_weather_key)
        val precipinationViewModel = PrecipinationViewModel(precipinationService, apiKey)
        precipinationViewModel.fetchWeatherData(getString(R.string.location))

        setContent {
            PrecipinationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val weatherData by precipinationViewModel.weatherInfo.observeAsState()

                    PrecipinationScreen(modifier = Modifier.padding(innerPadding), weatherData = weatherData)
                }
            }
        }
    }
}

fun tempConversion(kelvin : Double): Int{
    val fahrenheit = ((kelvin - 273.15) * 9/5 + 32).toInt()
    return fahrenheit
}

@Composable
fun PrecipinationScreen(modifier: Modifier = Modifier, weatherData: WeatherInfo?) {

    Column(modifier = modifier.fillMaxSize()){
        TopBar()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 128.dp),
        horizontalAlignment = Alignment.Start
    ) {
        CurrentLocation()
        Spacer(modifier = Modifier.height(16.dp))
        CurrentWeather(weatherData)
        Spacer(modifier = Modifier.height(16.dp))
        WeatherStats(weatherData)
    }

}

@Composable
fun TopBar(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.LightGray)
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        )
    }
}

@Composable
fun CurrentLocation(){
    Text(
        text = stringResource(id = R.string.location),
        fontSize = 20.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun CurrentWeather(weatherData : WeatherInfo?) {
    val temp = weatherData?.main?.temp?.let { tempConversion(it) } ?: 0
    val feelsLike = weatherData?.main?.feelsLike?.let { tempConversion(it) } ?: 0

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Column(modifier = Modifier) {
            Text(
                text = stringResource(id = R.string.temperature, temp),
                fontSize = 72.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = stringResource(id = R.string.feels_like, feelsLike),
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.width(112.dp))

        AndroidView(
            modifier = Modifier
                .size(72.dp)
                .padding(start = 8.dp),
            factory = { context ->
                ImageView(context).apply {
                    setImageResource(R.drawable.clear_skies)
                }
            }
        )

    }
}

@Composable
fun WeatherStats(weatherData : WeatherInfo?) {
    val low = weatherData?.main?.tempMin?.let { tempConversion(it) } ?: 0
    val high = weatherData?.main?.tempMax?.let { tempConversion(it) } ?: 0

    Column(modifier = Modifier.padding(horizontal = 32.dp)) {
        Text(text = stringResource(id = R.string.low_temp, low), fontSize = 20.sp)
        Text(text = stringResource(id = R.string.high_temp, high), fontSize = 20.sp)
        Text(text = stringResource(id = R.string.humidity), fontSize = 20.sp)
        Text(text = stringResource(id = R.string.pressure), fontSize = 20.sp)
    }
}

fun createRetrofitService(): PrecipinationService {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(HttpLoggingInterceptor.Level.BODY)

    val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    return Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(PrecipinationService::class.java)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrecipinationPreview() {
    PrecipinationTheme {
        //PrecipinationScreen()
    }
}