package com.yadu.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yadu.weatherapp.data.model.Weather
import com.yadu.weatherapp.data.repository.WeatherRepository


class WeatherViewModel:ViewModel(){
     lateinit var mutableWeatherLiveData: MutableLiveData<Weather>
    private lateinit var weatherRepository:WeatherRepository

    fun init() {
        if (mutableWeatherLiveData != null) {
            return
        }
       weatherRepository = WeatherRepository().getInstance()
        mutableWeatherLiveData = weatherRepository.weather
    }

    fun getWeather(lat: Double, lon: Double) {
        weatherRepository.getWeather(lat, lon)
    }

}