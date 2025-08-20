package com.amanthebreaker.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amanthebreaker.weatherapp.ui.SettingsPage
import com.amanthebreaker.weatherapp.ui.WeatherApp
import com.amanthebreaker.weatherapp.ui.WeatherSearch
import com.amanthebreaker.weatherapp.ui.WeatherWelcomeScreen


@Composable
fun WeatherNavGraph(startDestination: String = WeatherAppDestination.WEL_ROUTE) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(WeatherAppDestination.WEL_ROUTE) {
            WeatherWelcomeScreen(
                onGetStarted = {
                    navController.navigate(WeatherAppDestination.HOME_ROUTE) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(WeatherAppDestination.HOME_ROUTE) {
            WeatherApp(
                onBackToWelcome = {
                    navController.navigate(WeatherAppDestination.WEL_ROUTE) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState       = true
                        }
                        launchSingleTop     = true
                        restoreState        = true
                    }
                },
                onClickToSettings = {
                    navController.navigate(WeatherAppDestination.SETTINGS)
                },
                onClickToWeatherSearch = {
                    navController.navigate(WeatherAppDestination.WEATHER_SEARCH)
                }
            )
        }
        composable(WeatherAppDestination.SETTINGS){
            SettingsPage()
        }

        composable(WeatherAppDestination.WEATHER_SEARCH) {
            WeatherSearch()
        }
    }
}
