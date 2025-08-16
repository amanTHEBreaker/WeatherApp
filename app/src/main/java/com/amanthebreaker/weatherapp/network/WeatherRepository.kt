package com.amanthebreaker.weatherapp.network

import com.amanthebreaker.weatherapp.data.WeatherResponse

class WeatherRepository(private val api: OpenWeatherApi) {

    suspend fun fetchCurrentWeather(city: String, apiKey: String): Result<WeatherResponse> {
        return try {
            val resp = api.getCurrentWeather(city, apiKey, units = "metric")
            Result.success(resp)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
