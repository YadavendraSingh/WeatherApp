package com.yadu.weatherapp.data.repository

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.yadu.weatherapp.data.Constants
import com.yadu.weatherapp.data.api.WeatherClient
import com.yadu.weatherapp.data.api.WeatherInterface
import com.yadu.weatherapp.data.model.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val TAG = "Weather Service"
class DataService : IntentService("DataService") {

    override fun onHandleIntent(intent: Intent?) {

        when (intent?.action) {
            Constants.WeatherData -> {
                val lat = intent.getDoubleExtra("lat", 0.0)
                val lon = intent.getDoubleExtra("lon", 0.0)
                handleActionWeather(lat, lon)
            }
        }
    }

    private fun handleActionWeather(lat: Double, lon: Double) {
        val apiService: WeatherInterface =
            WeatherClient.getClient()

        val call: Call<Weather> = apiService.getWeatherResults(lat, lon)
        call.enqueue(object : Callback<Weather?> {
            override fun onResponse(
                call: Call<Weather?>?,
                response: Response<Weather?>
            ) {
                val weather: Weather = response.body()!!
                sendIntent(weather)
                Log.d(TAG, "Weather: ${weather.toString()}")
            }

            override fun onFailure(call: Call<Weather?>?, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
            }
        })
    }

    private fun sendIntent(weather: Weather) {
        val intent = Intent(Constants.WeatherData)
        intent.putExtra("weather", weather)
        LocalBroadcastManager.getInstance(this@DataService).sendBroadcast(intent)
    }

}
