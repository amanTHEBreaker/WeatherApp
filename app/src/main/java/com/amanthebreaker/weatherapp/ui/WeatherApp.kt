package com.amanthebreaker.weatherapp.ui


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.amanthebreaker.weatherapp.ui.theme.LocalGradientColors
import com.amanthebreaker.weatherapp.ui.theme.WeatherAppTheme
import com.amanthebreaker.weatherapp.util.LocationHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun WeatherApp(
    onBackToWelcome: () -> Unit,
    onClickToSettings: () -> Unit,
    onClickToWeatherSearch : () -> Unit,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val weatherUiState by weatherViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val gradientColors = LocalGradientColors.current
    val backgroundBrush = Brush.verticalGradient(gradientColors)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // state for rationale dialog
    val showRationaleDialog = remember { mutableStateOf(false) }

    // launcher to open settings (used for permanent denial)
    val openSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { /* nothing to do on return */ }

    // permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        coroutineScope.launch {
            if (granted && isLocationPermissionGranted(context)) {
                // permission granted - fetch location
                fetchCityAndTrigger(weatherViewModel, context, snackbarHostState)
            } else {
                // permission denied — determine if we should show rationale or open settings
                val activity = (context as? Activity)
                val shouldShowRationale = activity?.let { act ->
                    ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.ACCESS_FINE_LOCATION)
                } ?: false

                if (shouldShowRationale) {
                    showRationaleDialog.value = true
                } else {
                    // permanently denied (or permission policy) -> guide user to settings
                    snackbarHostState.showSnackbar("Location permission permanently denied. Open app settings.")
                    openAppSettings(context, openSettingsLauncher)
                }
            }
        }
    }

    // local function to start the locate flow
    fun startLocateFlow() {
        // if permission already present, fetch directly
        if (isLocationPermissionGranted(context)) {
            coroutineScope.launch {
                fetchCityAndTrigger(weatherViewModel, context, snackbarHostState)
            }
            return
        }
        // otherwise request permission
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    fun isLocationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    // Dialog that explains why we need permission (when system suggests it)
    if (showRationaleDialog.value) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog.value = false },
            title = { Text("Location required") },
            text = { Text("We need your location to automatically detect your city and show weather.") },
            confirmButton = {
                TextButton(onClick = {
                    showRationaleDialog.value = false
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) { Text("Allow") }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog.value = false }) { Text("Cancel") }
            }
        )
    }

    // LaunchedEffect to propagate snack bar messages from ViewModel events if you already have that
    LaunchedEffect(Unit) {
        weatherViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState, modifier = Modifier.imePadding()) },
        bottomBar = {
            WeatherBottomBarNoFab(
                onLeftClick = { startLocateFlow() },          // locate action
                onCenterClick = onClickToWeatherSearch,
                onRightClick = onClickToSettings
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(12.dp)
                .clip(RoundedCornerShape(28.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(padding)
            ) {
                Spacer(Modifier.height(16.dp))
                    if (weatherUiState.iconDay != null) {
                        AsyncImage(
                            model = weatherUiState.iconDay,
                            contentDescription = weatherUiState.iconDay,
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    if (weatherUiState.cityName.isBlank() || weatherUiState.temp == "N/A") {
                        Text(
                            text = "Click on locate button to fetch the Weather as per location",
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
                        labelValue = "City Name",
                        icon = Icons.Outlined.LocationOn,
                        value = weatherUiState.cityName,
                        onValueChange = { weatherViewModel.updateCityName(it) },
                        enabled = false // disable while loading
                    )


                }
            }

            if (weatherUiState.isLoading) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }


/** Helpers used above **/

private fun isLocationPermissionGranted(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    return fine == PermissionChecker.PERMISSION_GRANTED || coarse == PermissionChecker.PERMISSION_GRANTED
}

private fun openAppSettings(context: Context, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    launcher.launch(intent)
}

private suspend fun fetchCityAndTrigger(
    viewModel: WeatherViewModel,
    context: Context,
    snackbarHostState: SnackbarHostState
) {
    try {
        val city = LocationHelper.getCityFromLocation(context) // your helper, runs on IO inside
        if (!city.isNullOrBlank()) {
            viewModel.updateCityName(city)
            viewModel.getForecast()
        } else {
            snackbarHostState.showSnackbar("Couldn't resolve city from location")
        }
    } catch (e: Exception) {
        snackbarHostState.showSnackbar("Failed to get location: ${e.localizedMessage ?: "unknown"}")
    }
}


@Composable
fun WeatherBottomBarNoFab(
    onLeftClick: () -> Unit,
    onCenterClick: () -> Unit,
    onRightClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barHeight = 72.dp
    // outer box to allow the center button to overlap the Surface
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(
                barHeight + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // the rounded bottom bar background
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            color = Color.White.copy(alpha = 0.08f),
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left item
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onLeftClick() }
                        .padding(vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "location",
                        tint = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Locate", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                }

                // placeholder to keep space for center button (no visual element required)
                Spacer(Modifier.width(64.dp))

                // Right item
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onRightClick() }
                        .padding(vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "menu",
                        tint = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Menu", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }

        // Center circular button implemented as a regular Surface (not a FloatingActionButton)
        Surface(
            shape = CircleShape,
            tonalElevation = 8.dp,
            color = Color.White,
            modifier = Modifier
                .size(68.dp)
                .offset(y = (-barHeight / 2)) // overlap the bar visually
                .clickable { onCenterClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "center",
                    tint = Color(0xFF6A4CFF), // icon tint, change as needed
                    modifier = Modifier.size(28.dp)
                )
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
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
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
        WeatherApp(onBackToWelcome = {}, onClickToSettings = {}, onClickToWeatherSearch = {})
    }
}
