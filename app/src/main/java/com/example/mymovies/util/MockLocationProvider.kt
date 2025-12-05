package com.example.mymovies.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.SystemClock
import android.util.Log
import com.example.mymovies.util.hasPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
class MockLocationProvider(private val providerName: String, private val context: Context) {
    companion object{
        const val TAG = "MockLocationProvider"
    }

    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun enableProvider(){ fusedLocationProviderClient.setMockMode(true) }
    fun disableProvider(){ fusedLocationProviderClient.setMockMode(false) }

    fun pushLocation(lat: Double?, lon: Double?) {
        if (!context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            && !context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            return
        }

        val location = Location(providerName).apply {
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            time = System.currentTimeMillis()
            latitude = lat ?: 0.0
            longitude = lon ?: 0.0
            altitude = 0.0
            accuracy = 1.0f
        }

        fusedLocationProviderClient.setMockLocation(location)
            .addOnSuccessListener { Log.d(TAG,"location mocked") }
            .addOnFailureListener { Log.d(TAG,"mock failed") }

    }

}