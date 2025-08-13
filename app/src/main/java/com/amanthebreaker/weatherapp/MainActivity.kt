package com.amanthebreaker.weatherapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amanthebreaker.weatherapp.ui.theme.WeatherAppTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherApp()
        }
    }
}

@Composable
fun WeatherApp(
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val weatherUiState by weatherViewModel.uiState.collectAsState()

    val context = LocalContext.current

    WeatherAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                HeadingMaker("Today's Forecast") {
                    Text(
                        text = weatherUiState.temp + " " + weatherUiState.humidity,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingFromBaseline(top = 30.dp, bottom = 10.dp)
                            .padding(horizontal = 10.dp)
                    )

                    MyTextFieldComponent(
                        labelValue = "Enter City Name",
                        icon = Icons.Outlined.LocationOn,
                        value= weatherViewModel.userInput,
                        onValueChange = {
                            weatherViewModel.updateUserInput(it)
                        }
                    )

                    Button(
                        onClick = {
                            weatherViewModel.getForcast()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
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
        }
    }
}


@Composable
fun MyTextFieldComponent(labelValue: String,
                         icon: ImageVector,
                         value : String,
                         onValueChange : (String) -> Unit) {

    OutlinedTextField(
        label = {
            Text(text = labelValue)
        },
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "profile"
            )
        },
        keyboardOptions = KeyboardOptions.Default
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
            style = MaterialTheme.typography.titleMedium,
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
    WeatherAppTheme  {
        WeatherApp()
    }
}

