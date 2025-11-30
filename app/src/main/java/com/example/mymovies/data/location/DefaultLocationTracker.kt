package com.example.mymovies.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.example.mymovies.util.Constants.Companion.CURRENT_LOCATION_ERROR
import com.example.mymovies.util.Constants.Companion.LAST_LOCATION_ERROR
import com.example.mymovies.util.hasPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.IOException


class DefaultLocationTracker(
    private val locationClient: FusedLocationProviderClient,
    private val context: Context
) : LocationTracker {

    private val cancellationToken = object : CancellationToken() {
        override fun isCancellationRequested(): Boolean {
            return false
        }

        override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
            return CancellationTokenSource().token
        }

    }

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    @ExperimentalCoroutinesApi
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {
        if (!context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ||
            !context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
            !isGpsEnabled
        ) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            locationClient.getCurrentLocation(
                LocationRequest.PRIORITY_LOW_POWER, cancellationToken
            ).apply {
                if (isComplete) {
                    if (isSuccessful) {
                        continuation.resume(result, null)
                    } else {
                        throw IOException(CURRENT_LOCATION_ERROR)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener { currentLocation ->
                    continuation.resume(currentLocation, null)
                }
                addOnFailureListener { exception ->
                    throw exception
                }
                addOnCanceledListener {
                    continuation.cancel()
                }
            }
        }
    }


    @ExperimentalCoroutinesApi
    @SuppressLint("MissingPermission")
    override suspend fun getLastLocation(): Location? {
        if (!context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ||
            !context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
            !isGpsEnabled
        ) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            locationClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful) {
                        continuation.resume(result, null)
                    } else {
                        throw IOException(LAST_LOCATION_ERROR)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener { lastLocation ->
                    continuation.resume(lastLocation, null)
                }
                addOnFailureListener { exception ->
                    throw exception
                }
                addOnCanceledListener {
                    continuation.cancel()
                }
            }
        }
    }
}