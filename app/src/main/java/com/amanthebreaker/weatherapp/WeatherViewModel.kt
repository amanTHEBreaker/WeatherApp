package com.amanthebreaker.weatherapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WeatherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(Weather())
    val uiState : StateFlow<Weather> = _uiState.asStateFlow()

    var userInput by mutableStateOf("")
        private set

//    private lateinit var currentState : String

    fun updateUserInput(guessedWord: String){
        userInput = guessedWord
    }

    fun getForcast() {
        if(userInput.equals("Nagpur",ignoreCase = true)) {
            val temperature = "50 C"
            val humidity = "40 PCM"
            _uiState.update {
                currentState ->
                    currentState.copy(temp = temperature, humidity = humidity)
            }
        } else {
            _uiState.update {
                currentState ->
                currentState.copy(temp = "NO value", humidity = "no value")
            }
        }
    }



}