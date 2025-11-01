package com.example.mymovies.data.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.mymovies.R
import com.example.mymovies.util.Constants.Companion.CURRENT_LOCATION_ERROR

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.IOException


class DefaultLocationTracker(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application): LocationTracker {

    @ExperimentalCoroutinesApi
    override suspend fun getCurrentLocation(): Location? {
        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val cancellationToken = object : CancellationToken() {
            override fun isCancellationRequested(): Boolean {
                return false
            }

            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                return CancellationTokenSource().token
            }

        }

        val locationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission
            || !isGpsEnabled) { return null }

        return suspendCancellableCoroutine { continuation ->
            locationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationToken).apply {
                if (isComplete) {
                    if (isSuccessful) {
                        continuation.resume(result, throw IOException(CURRENT_LOCATION_ERROR))
                    } else {
                        continuation.resume( null, throw IOException(CURRENT_LOCATION_ERROR))
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener { currentLocation ->
                    continuation.resume(currentLocation, throw IOException(CURRENT_LOCATION_ERROR))
                }
                addOnFailureListener { e ->
                    continuation.resume(null, throw e)
                }
                addOnCanceledListener {
                    continuation.cancel(throw IOException(CURRENT_LOCATION_ERROR))
                }
            }
        }
    }
}
