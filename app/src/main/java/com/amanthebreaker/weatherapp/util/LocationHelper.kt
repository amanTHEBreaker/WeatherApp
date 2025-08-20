package com.amanthebreaker.weatherapp.util

// LocationHelper.kt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LocationHelper {

    suspend fun getCurrentLocation(context: Context): Location? =
        suspendCancellableCoroutine { cont ->
            try {
                val fused = LocationServices.getFusedLocationProviderClient(context)
                val cts = CancellationTokenSource()
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@suspendCancellableCoroutine
                }
                fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                    .addOnSuccessListener { location ->
                        cont.resume(location)
                    }
                    .addOnFailureListener { ex ->
                        cont.resumeWithException(ex)
                    }
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }

    suspend fun getCityFromLocation(context: Context): String? {
        return try {
            val loc = getCurrentLocation(context) ?: return null
            reverseGeocode(context, loc.latitude, loc.longitude)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun reverseGeocode(context: Context, lat: Double, lon: Double): String? =
        withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                // This is blocking, so run on IO
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                val addr = addresses?.firstOrNull()
                // prefer locality (city), then subAdminArea/adminArea
                addr?.let {
                    it.locality ?: it.subAdminArea ?: it.adminArea ?: it.featureName
                }
            } catch (e: Exception) {
                null
            }
        }
}
