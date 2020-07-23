package com.yadu.weatherapp.data.api

import com.yadu.weatherapp.data.Constants.API_KEY
import com.yadu.weatherapp.data.Constants.BASE_URL
import com.yadu.weatherapp.data.Constants.EXCLUDE
import com.yadu.weatherapp.data.Constants.METRIC
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object WeatherClient {

    fun getClient():WeatherInterface{

        val requestInterceptor = Interceptor { chain ->
            // Interceptor take only one argument which is a lambda function so parenthesis can be omitted

            val url = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("appid", API_KEY)
                .addQueryParameter("units", METRIC)
                .addQueryParameter("exclude", EXCLUDE)
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherInterface::class.java)
    }
}