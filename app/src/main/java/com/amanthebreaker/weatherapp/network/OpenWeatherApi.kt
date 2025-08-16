package com.amanthebreaker.weatherapp.network

import com.amanthebreaker.weatherapp.data.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,                 // city name like "Nagpur"
        @Query("appid") apiKey: String,           // your API key
        @Query("units") units: String = "metric"  // metric for °C
    ): WeatherResponse
}