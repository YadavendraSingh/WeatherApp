package com.yadu.weatherapp.data.api

import com.yadu.weatherapp.data.model.Weather
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherInterface {

    @GET("/onecall?")
    fun getWeatherResults(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Single<Weather>
}