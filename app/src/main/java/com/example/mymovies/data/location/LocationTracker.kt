package com.example.mymovies.data.location

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?

    suspend fun getLastLocation(): Location?
}