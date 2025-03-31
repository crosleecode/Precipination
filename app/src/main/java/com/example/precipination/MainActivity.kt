package com.example.precipination

import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrecipinationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PrecipinationScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PrecipinationScreen(modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxSize()){
        TopBar();
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 128.dp),
        horizontalAlignment = Alignment.Start
    ) {
        CurrentLocation();
        Spacer(modifier = Modifier.height(16.dp))
        CurrentWeather();
        Spacer(modifier = Modifier.height(16.dp))
        WeatherStats();
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
fun CurrentWeather(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Column(modifier = Modifier) {
            Text(
                text = stringResource(id = R.string.temperature),
                fontSize = 72.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = stringResource(id = R.string.feels_like),
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
fun WeatherStats(){
    Column(modifier = Modifier.padding(horizontal = 32.dp)) {
        Text(text = stringResource(id = R.string.low_temp), fontSize = 20.sp)
        Text(text = stringResource(id = R.string.high_temp), fontSize = 20.sp)
        Text(text = stringResource(id = R.string.humidity), fontSize = 20.sp)
        Text(text = stringResource(id = R.string.pressure), fontSize = 20.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrecipinationPreview() {
    PrecipinationTheme {
        PrecipinationScreen()
    }
}