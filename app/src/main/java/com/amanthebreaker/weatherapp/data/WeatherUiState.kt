package com.amanthebreaker.weatherapp.data

data class WeatherUiState(
    val cityName: String = "",
    val temp: String = "",
    val humidity: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
