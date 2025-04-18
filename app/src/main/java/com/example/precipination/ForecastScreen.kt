package com.example.precipination


import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight


@Composable
fun ForecastScreen(forecastList: List<WeatherForecast>, onBackClicked: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(52.dp))

        ForecastTopBar(onBackClicked)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()

        ) {
            itemsIndexed(forecastList) {index, forecast ->
                ForecastRow(forecast, index)
            }
        }
    }
}

@Composable
fun ForecastTopBar(onBackClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF3C3B6E))
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Monospace,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        )

        Button(
            onClick = onBackClicked,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB22234)),
            border = BorderStroke(2.dp, Color.White),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 32.dp)
        ) {
            Text(stringResource(id = R.string.back_button), color = Color.White)
        }
    }
}

@Composable
fun ForecastRow(forecast: WeatherForecast, index:Int) {
    val dayTemp = tempConversion(forecast.temp.day)
    val minTemp = tempConversion(forecast.temp.min)
    val maxTemp = tempConversion(forecast.temp.max)
    val icon = forecast.weather.firstOrNull()?.icon ?: ""
    val iconId = getWeatherIcon(icon)

    val backgroundModifier = if (index % 2 == 0) {
        Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFE06666),
                    Color.Transparent,
                    Color(0xFFE06666)
                )
            )
        )
    } else {
        Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White,
                    Color.Transparent,
                    Color.White,
                )
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(120.dp).then(backgroundModifier)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(120.dp).padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                text = stringResource(R.string.temperature, dayTemp),
                fontSize = 32.sp
            )

            AndroidView(
                factory = { context ->
                    ImageView(context)
                },
                update = { imageView ->
                    imageView.setImageResource(iconId)
                },
                modifier = Modifier.size(64.dp)
            )

            Text(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                text = stringResource(R.string.high_low, maxTemp, minTemp),
                fontSize = 20.sp
            )
            Text(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                text = forecast.date,
                fontSize = 20.sp
            )
        }
    }
}