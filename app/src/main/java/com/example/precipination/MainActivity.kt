package com.example.precipination

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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

//Assignment 4
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

//assignment 5
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val precipinationService = createRetrofitService()
        val apiKey = resources.getString(R.string.open_weather_key)
        val precipinationViewModel = PrecipinationViewModel(precipinationService, apiKey, Dispatchers.IO)
        precipinationViewModel.fetchWeatherData(getString(R.string.default_zip))

        setContent {

            PrecipinationTheme {
                Box(modifier = Modifier.fillMaxSize()) {

                    Image(
                        painter = painterResource(id = R.drawable.rain_precipination),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent) { innerPadding ->
                        val weatherData by precipinationViewModel.weatherInfo.observeAsState()
                        val alert by precipinationViewModel.alert.observeAsState()

                        val navController = rememberNavController()
                        val forecastData by precipinationViewModel.forecastInfo.observeAsState()

                        val displayAlert = remember { mutableStateOf(false) }

                        LaunchedEffect(alert) {
                            displayAlert.value = alert != null
                        }

                        NavHost(navController = navController, startDestination = "main") {
                            composable("main") {
                                PrecipinationScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    weatherData = weatherData,
                                    onSubmit = { zipCode ->
                                        precipinationViewModel.fetchWeatherData(
                                            zipCode
                                        )
                                    },
                                    onForecastClick = {
                                        precipinationViewModel.fetchForecast()
                                        navController.navigate("forecast")
                                    },
                                    precipinationViewModel
                                )
                            }

                            composable("forecast") {
                                ForecastScreen(
                                    forecastList = forecastData ?: emptyList(),
                                    onBackClicked = { navController.popBackStack() }
                                )
                            }
                        }
                        InvalidZipAlert(alert, displayAlert, precipinationViewModel)

                    }
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
fun PrecipinationScreen(
    modifier: Modifier = Modifier,
    weatherData: WeatherInfo?,
    onSubmit: (String)->Unit,
    onForecastClick: () -> Unit,
    precipinationViewModel: PrecipinationViewModel
) {

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(52.dp))

        TopBar(onForecastClick = onForecastClick)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 128.dp),
        horizontalAlignment = Alignment.Start
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            CurrentLocation(city = weatherData?.name, onSubmit = onSubmit, precipinationViewModel)
        }
        Spacer(modifier = Modifier.height(16.dp))
        CurrentWeather(weatherData)
        Spacer(modifier = Modifier.height(16.dp))
        WeatherStats(weatherData)

    }

}

@Composable
fun TopBar(onForecastClick: () -> Unit){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF3C3B6E))
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        )

        Button(
            onClick = onForecastClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB22234)),
            border = BorderStroke(2.dp, Color.White),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 32.dp)
        ) {
            Text(stringResource(id = R.string.forecast_button), color = Color.White)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CurrentLocation(city : String?, onSubmit: (String) -> Unit, precipinationViewModel: PrecipinationViewModel){
    val zipCode = remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {

        val context = LocalContext.current
        val locPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
                Log.d("Location Permission", "Already granted")
        }

        val notificationPermissionGranted = remember { mutableStateOf(false) }

        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            notificationPermissionGranted.value = isGranted
        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            TextField(
                value = zipCode.value,
                onValueChange = {
                    zipCode.value = it
                },
                label = {Text(stringResource(R.string.zip_code))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                onSubmit(zipCode.value) },
                modifier = Modifier.width(96.dp)
            ) {
                Text(stringResource(R.string.submit_button))
            }

            IconButton(onClick = {
                val locPermissionStatus = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                val notificationPermissionStatus = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                )

                if (locPermissionStatus != PackageManager.PERMISSION_GRANTED) {
                    locPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }

                if (notificationPermissionStatus != PackageManager.PERMISSION_GRANTED) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }


                if (locPermissionStatus == PackageManager.PERMISSION_GRANTED && (notificationPermissionStatus == PackageManager.PERMISSION_GRANTED)) {

                    context.startForegroundService(Intent(context, NotificationService::class.java))
                    getCurrentLocation(context) { lat, lon ->
                        precipinationViewModel.fetchCurrentLocationWeather(lat, lon)
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Outlined.MyLocation,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        city?.let {
            Text(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                text = stringResource(R.string.city, it),
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CurrentWeather(weatherData : WeatherInfo?) {
    val temp = weatherData?.main?.temp?.let { tempConversion(it) } ?: 0
    val feelsLike = weatherData?.main?.feelsLike?.let { tempConversion(it) } ?: 0

    val iconCode = weatherData?.weather?.firstOrNull()?.icon
    val iconImage = iconCode?.let { getWeatherIcon(it) } ?: R.drawable.clear_skies_d

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Column(modifier = Modifier) {
            Text(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                text = stringResource(id = R.string.temperature, temp),
                fontSize = 72.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                text = stringResource(id = R.string.feels_like, feelsLike),
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.width(112.dp))

        AndroidView(
            factory = { context ->
                ImageView(context)
            },
            update = { imageView ->
                imageView.setImageResource(iconImage)
            },
            modifier = Modifier
                .size(96.dp)
                .padding(start = 0.dp)
        )

    }
}

@Composable
fun WeatherStats(weatherData : WeatherInfo?) {
    val low = weatherData?.main?.tempMin?.let { tempConversion(it) } ?: 0
    val high = weatherData?.main?.tempMax?.let { tempConversion(it) } ?: 0
    val humidity = weatherData?.main?.humidity ?: 0
    val pressure = weatherData?.main?.pressure ?: 0

    Column(modifier = Modifier.padding(horizontal = 32.dp)) {
        Text(text = stringResource(id = R.string.low_temp, low), fontSize = 20.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Text(text = stringResource(id = R.string.high_temp, high), fontSize = 20.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Text(text = stringResource(id = R.string.humidity, humidity), fontSize = 20.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Text(text = stringResource(id = R.string.pressure, pressure), fontSize = 20.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}

fun getCurrentLocation(context: Context, onLocationReady: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationRequest = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setMaxUpdateAgeMillis(0) // Force fresh result
        .build()

    try {
        fusedLocationClient.getCurrentLocation(locationRequest, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onLocationReady(location.latitude, location.longitude)
                } else {
                    Log.e("Location", "Location is null")
                }
            }
            .addOnFailureListener {
                Log.e("Location", "Location Failed", it)
            }
    } catch (e: SecurityException) {
        Log.e("Location", "Missing permission", e)
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

@Composable
fun InvalidZipAlert(alert : String?, displayAlert : MutableState<Boolean>, precipinationViewModel: PrecipinationViewModel){
    if(displayAlert.value && alert != null){
        AlertDialog(
            onDismissRequest = {displayAlert.value = false},
            confirmButton = {
                Button(
                    onClick = {
                        displayAlert.value = false
                        precipinationViewModel.clearAlert()
                    },
                    modifier = Modifier.width(96.dp)){
                    Text(stringResource(R.string.ok_button))
                }
            },
            title = {Text(stringResource(R.string.alert))},
            text = {Text(alert)}

        )
    }
}

fun getWeatherIcon(iconCode: String): Int {
    return when (iconCode) {
        "01d" -> R.drawable.clear_skies_d
        "01n" -> R.drawable.clear_skies_n
        "02d", "03d" -> R.drawable.light_clouds_d
        "02n", "03n" -> R.drawable.light_clouds_n
        "04d", "04n" -> R.drawable.cloudy
        "09d", "09n", "10d", "10n" -> R.drawable.rain
        "11d", "11n" -> R.drawable.thunder
        "13d", "13n" -> R.drawable.snow
        "50d", "50n" -> R.drawable.mist
        else -> R.drawable.clear_skies_n
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrecipinationPreview() {
    PrecipinationTheme {
        //PrecipinationScreen()
    }
}