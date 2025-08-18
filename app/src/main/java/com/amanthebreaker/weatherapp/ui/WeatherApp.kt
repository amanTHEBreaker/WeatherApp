package com.amanthebreaker.weatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.amanthebreaker.weatherapp.ui.theme.LocalGradientColors
import com.amanthebreaker.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WeatherApp(
    onBackToWelcome : () -> Unit,
    onClickToSettings : () -> Unit,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val weatherUiState by weatherViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val gradientColors = LocalGradientColors.current
    val backgroundBrush = Brush.verticalGradient(gradientColors)

    LaunchedEffect(Unit) {
        weatherViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.imePadding())
    }) {
        padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(12.dp)
                .clip(RoundedCornerShape(28.dp))
        )  {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                HeadingMaker("Today's Forecast") {

                    if(weatherUiState.iconDay != null) {
                        AsyncImage(
                            model = weatherUiState.iconDay,
                            contentDescription = weatherUiState.iconDay,
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    if (weatherUiState.cityName.isBlank() || weatherUiState.temp == "N/A") {
                        Text(
                            text = "Enter a city to see forecast",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.7f)),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .paddingFromBaseline(top = 30.dp, bottom = 10.dp)
                                .padding(horizontal = 10.dp)
                        )
                    } else {
                        Text(
                            text = "Temperature : ${weatherUiState.temp}  Humidity : ${weatherUiState.humidity}  ",
                            style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .paddingFromBaseline(top = 30.dp, bottom = 10.dp)
                                .padding(horizontal = 10.dp)
                        )
                    }


                    MyTextFieldComponent(
                        labelValue = "Enter City Name",
                        icon = Icons.Outlined.LocationOn,
                        value = weatherUiState.cityName,
                        onValueChange = {
                            weatherViewModel.updateCityName(it)
                        },
                        enabled = !weatherUiState.isLoading // disable while loading
                    )

                    Button(
                        onClick = {
                            weatherViewModel.getForecast()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        enabled = !weatherUiState.isLoading
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    shape = RoundedCornerShape(50.dp),
                                    color = Color.Black
                                )
                                .fillMaxWidth()
                                .heightIn(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Forcast", color = Color.White, fontSize = 20.sp)
                        }
                    }
                }
            }

            if (weatherUiState.isLoading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

    }
}


@Composable
fun MyTextFieldComponent(
    labelValue: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(labelValue, color = Color.White) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "location",
                tint = Color.White
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        enabled = enabled,
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
            cursorColor = Color.White
        )
    )
}


@Composable
fun HeadingMaker(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp)
        )
        content()
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF5F0EE)
@Composable
fun WeatherAppTEst() {
    WeatherAppTheme {
        WeatherApp(onBackToWelcome = {}, onClickToSettings = {})
    }
}
