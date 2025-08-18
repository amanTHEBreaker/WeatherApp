package com.amanthebreaker.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amanthebreaker.weatherapp.data.WeatherUiState
import com.amanthebreaker.weatherapp.network.RetrofitClient
import com.amanthebreaker.weatherapp.network.WeatherRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
}

private val weatherIconMap = mapOf(
    "clear sky" to "01",
    "few clouds" to "02",
    "scattered clouds" to "03",
    "broken clouds" to "04",
    "shower rain" to "09",
    "rain" to "10",
    "thunderstorm" to "11",
    "snow" to "13",
    "mist" to "50"
)


class WeatherViewModel(
    private val repo: WeatherRepository = WeatherRepository(RetrofitClient.api)
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState = _uiState.asStateFlow()

    // Channel for one-shot UI events
    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    fun updateCityName(name: String) {
        _uiState.update { it.copy(cityName = name, error = null) }
    }

    fun getForecast() {
        val city = _uiState.value.cityName.trim()
        if (city.isEmpty()) {
            _uiState.update { it.copy(error = "Enter a city") }
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar("Please enter city name"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val apiKey = ""
            if (apiKey.isBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "API key missing") }
                _uiEvent.send(UiEvent.ShowSnackbar("API Key Missing"))
                return@launch
            }

            val result = repo.fetchCurrentWeather(city, apiKey)
            result.fold(onSuccess = { resp ->

                val temp = resp.main?.temp?.let { String.format("%.1f°C", it) } ?: "N/A"
                val humidity = resp.main?.humidity?.let { "$it%" } ?: "N/A"
                val description = resp.weather?.firstOrNull()?.description ?: "N/A"

                val iconBase = weatherIconMap[description.lowercase()] ?: "01"
                val dayIcon     =   "https://openweathermap.org/img/wn/${iconBase}d@4x.png"
                val nightIcon   =   "https://openweathermap.org/img/wn/${iconBase}n@4x.png"

                _uiState.update {
                    it.copy(temp = temp, humidity = humidity, isLoading = false, iconDay = dayIcon, iconNight = nightIcon)
                }

            }, onFailure = { err ->
                val msg = when (err) {
                    is UnknownHostException -> "No internet connection (DNS failed). Check your network."
                    is SocketTimeoutException -> "Network timeout. Try again."
                    else -> "Failed to fetch weather: ${err.localizedMessage ?: "unknown error"}"
                }
                _uiState.update { it.copy(isLoading = false, error = msg) }
                _uiEvent.send(UiEvent.ShowSnackbar(msg))

            })
        }
    }
}