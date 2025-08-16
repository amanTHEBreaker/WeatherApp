package com.amanthebreaker.weatherapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState : StateFlow<WeatherUiState> = _uiState.asStateFlow()


//    private lateinit var currentState : String
    fun updateCityName(name: String) {
        _uiState.update { it.copy(cityName = name, error = null) }
    }


    fun getForecast() {
        val city = _uiState.value.cityName.trim()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // simulate network or call repository here
            delay(5000)

            if (city.equals("Nagpur", ignoreCase = true)) {
                _uiState.update {
                    it.copy(temp = "50 C", humidity = "40 PCM", isLoading = false)
                }
            } else {
                _uiState.update {
                    it.copy(temp = "NO value", humidity = "no value", isLoading = false)
                }
            }
        }
    }
}