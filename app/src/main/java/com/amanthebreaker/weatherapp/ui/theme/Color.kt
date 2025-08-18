package com.amanthebreaker.weatherapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


val md_theme_light_gradient = listOf(
    Color(0xFF1F213A), // dark navy
    Color(0xFF6A3CA8), // purple
    Color(0xFFB85DAF)  // pink
)

val md_theme_dark_gradient = listOf(
    Color(0xFF0F101C), // darker navy
    Color(0xFF43276D), // deep purple
    Color(0xFF7B3C77)  // muted pink
)

val md_theme_light_primary = Color(0xFF6A3CA8)
val md_theme_light_onPrimary = Color.White
val md_theme_light_background = Color(0xFFF5F0EE)
val md_theme_light_onBackground = Color(0xFF1F213A)

// Dark mode palette
val md_theme_dark_primary = Color(0xFFB85DAF)
val md_theme_dark_onPrimary = Color.Black
val md_theme_dark_background = Color(0xFF1F213A)
val md_theme_dark_onBackground = Color.White

val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground
)

val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground
)