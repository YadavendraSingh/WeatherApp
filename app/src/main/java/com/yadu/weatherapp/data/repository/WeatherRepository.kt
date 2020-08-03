package com.yadu.weatherapp.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.yadu.weatherapp.data.api.WeatherClient
import com.yadu.weatherapp.data.api.WeatherInterface
import com.yadu.weatherapp.data.model.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository(){

   lateinit var repo:WeatherRepository

    fun getInstance(): WeatherRepository {
        if(repo!=null){
            repo = WeatherRepository()
        }

        return repo
    }

    val weather = MutableLiveData<Weather>()

     fun getWeather(lat: Double, lon: Double) {
        val apiService: WeatherInterface =
            WeatherClient.getClient()

        val call: Call<Weather> = apiService.getWeatherResults(lat, lon)
        call.enqueue(object : Callback<Weather?> {
            override fun onResponse(
                call: Call<Weather?>?,
                response: Response<Weather?>
            ) {
                 weather.value = response.body()!!

                Log.d(TAG, "Weather: ${weather.toString()}")
            }

            override fun onFailure(call: Call<Weather?>?, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
            }
        })
    }

}