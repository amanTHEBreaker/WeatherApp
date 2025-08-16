package com.amanthebreaker.weatherapp


data class WeatherResponse(
    val name: String?,
    val main: Main?,
    val weather: List<WeatherDescription>?
)

data class Main(
    val temp: Double?,
    val humidity: Int?
)

data class WeatherDescription(
    val main: String?,
    val description: String?
)
