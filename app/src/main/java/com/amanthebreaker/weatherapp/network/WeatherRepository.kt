package com.amanthebreaker.weatherapp.network

import android.util.Log
import com.amanthebreaker.weatherapp.data.WeatherResponse

class WeatherRepository(private val api: OpenWeatherApi) {

    suspend fun fetchCurrentWeather(city: String, apiKey: String): Result<WeatherResponse> {
        return try {
            val resp = api.getCurrentWeather(city, apiKey, units = "metric")
            Log.i("onResponse", resp.toString())
            Result.success(resp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
