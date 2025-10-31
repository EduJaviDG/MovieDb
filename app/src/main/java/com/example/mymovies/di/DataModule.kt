package com.example.mymovies.di

import com.example.mymovies.data.repositories.MovieRepository
import com.example.mymovies.data.datasources.local.MovieLocalDataSource
import com.example.mymovies.data.datasources.remote.MovieRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    fun movieRepositoryProvider(
        movieRemoteDataSource: MovieRemoteDataSource,
        movieLocalDataSource: MovieLocalDataSource
    ): MovieRepository =
        MovieRepository(movieRemoteDataSource, movieLocalDataSource)

}