package com.example.precipination

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class NotificationService : Service(){
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showPlaceholderNotification()

        val retrofitService = createRetrofitService()
        val apiKey = getString(R.string.open_weather_key)
        val notificationViewModel = NotificationViewModel(retrofitService, apiKey)

        getCurrentLocation(this) { lat, lon ->
            notificationViewModel.fetchWeatherByLocation(lat, lon) { weather ->
                if (weather != null) {
                    updateNotification(weather)
                }
            }
        }

        return START_STICKY
    }

    private fun showPlaceholderNotification() {
        val notification = NotificationCompat.Builder(this, "notification_channel")
            .setSmallIcon(R.drawable.cloudy)
            .setContentTitle(getString(R.string.getting_weather))
            .setOngoing(true)
            .setContentIntent(createPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(1, notification)
    }

    private fun updateNotification(weather: WeatherInfo) {
        val temp = tempConversion(weather.main.temp)
        val title = "${weather.name} Weather"
        val text = "${weather.weather.firstOrNull()?.main ?: "Weather"} • $temp°F"
        val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
        val iconId = getWeatherIcon(iconCode)

        val notification = NotificationCompat.Builder(this, "notification_channel")
            .setSmallIcon(iconId)
            .setContentTitle(title)
            .setContentText(text)
            .setOngoing(true)
            .setContentIntent(createPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(1, notification)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(this,0, intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "notification_channel",
            getString(R.string.precipination_notifications),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }


}