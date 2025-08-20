package com.amanthebreaker.weatherapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amanthebreaker.weatherapp.R
import com.amanthebreaker.weatherapp.ui.theme.backgroundBrush1

@Composable
fun WeatherWelcomeScreen(
    onGetStarted: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush1)
            .padding(12.dp)
            .clip(RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_weather_illustrations),
                contentDescription = stringResource(id = R.string.weather_illustration_cd),
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 5.dp),
                contentScale = ContentScale.Fit
            )


            // Title: "Weather"
            Text(
                text = stringResource(id = R.string.weather_title),
                fontSize = 60.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
            )

            // Subtitle: "ForeCasts" with golden color
            Text(
                text = "ForeCasts",
                fontSize = 50.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFF0C02E), // golden
                modifier = Modifier.padding(top = 6.dp, bottom = 28.dp),

            )

            // Button
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 280.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0C02E)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "Get Start",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF18324A) // dark navy-ish text on gold button
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewWeatherWelcome() {
    MaterialTheme {
        WeatherWelcomeScreen(onGetStarted = {})
    }
}