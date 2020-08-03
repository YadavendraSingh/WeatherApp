package com.yadu.weatherapp.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.yadu.weatherapp.data.api.WeatherClient
import com.yadu.weatherapp.data.api.WeatherInterface
import com.yadu.weatherapp.data.model.Weather
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WeatherNetworkDataSource (
    private val apiService: WeatherInterface, val lat: Double, val lon:Double, private val compositeDisposable: CompositeDisposable
){

    private val _downloadedCurrentWeather = MutableLiveData<Weather>()

  /*  fun loadWeatherData(){
        compositeDisposable.add(apiService.getWeatherResults(lat, lon)
            .subscribeOn(Schedulers.io())
            .subscribe({
                _downloadedCurrentWeather.postValue(it)
                //insertData(it, page)
                //networkState.postValue(NetworkState.LOADED)
            },
                {
                    //networkState.postValue(NetworkState.ERROR)
                    Log.e("ImageDataSource", it.message)
                    //load from database
                    //getFromDataBaseInitial(callback)
                }))
    }*/
}