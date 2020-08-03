package com.yadu.weatherapp.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.yadu.weatherapp.data.api.WeatherInterface
import com.yadu.weatherapp.data.model.Weather
import io.reactivex.disposables.CompositeDisposable

class WeatherNetworkDataSourceFactory(private val apiService: WeatherInterface,val lat: Double, val lon:Double, private val compositeDisposable: CompositeDisposable)
    /*: DataSource.Factory<Int, Weather>()*/ {

   /* val weatherLiveDataSource =  MutableLiveData<WeatherNetworkDataSource>()

    override fun create(): DataSource<Int, Weather> {
        val weatherDataSource = WeatherNetworkDataSource(apiService, lat, lon, compositeDisposable)

        weatherLiveDataSource.postValue(weatherDataSource)
        return weatherDataSource
    }*/
}