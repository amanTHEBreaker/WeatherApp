package com.amanthebreaker.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.flow.receiveAsFlow

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
}

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

            val apiKey = "16349da9f3fbd51a63f0479e2892d828"
            if (apiKey.isBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "API key missing") }
                _uiEvent.send(UiEvent.ShowSnackbar("API Key Missing"))
                return@launch
            }

            val result = repo.fetchCurrentWeather(city, apiKey)
            result.fold(onSuccess = { resp ->
                val temp = resp.main?.temp?.let { String.format("%.1f°C", it) } ?: "N/A"
                val humidity = resp.main?.humidity?.let { "$it%" } ?: "N/A"
                _uiState.update { it.copy(temp = temp, humidity = humidity, isLoading = false) }
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