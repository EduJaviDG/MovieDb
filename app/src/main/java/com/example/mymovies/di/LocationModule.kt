package com.example.mymovies.di

import android.app.Application
import android.content.Context
import com.example.mymovies.data.location.DefaultLocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(app: Application)
            : FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(app)

    @Singleton
    @Provides
    fun provideLocationTracker(
        fusedLocationProviderClient: FusedLocationProviderClient,
        @ApplicationContext context: Context
    ): DefaultLocationTracker =
        DefaultLocationTracker(fusedLocationProviderClient, context)

}