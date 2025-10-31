package com.example.mymovies.di

import com.example.mymovies.data.datasources.remote.MovieRemoteClient
import com.example.mymovies.data.datasources.remote.MovieRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun movieDbClientProvider(): MovieRemoteClient = MovieRemoteClient()

    @Singleton
    @Provides
    fun movieRemoteDataSourceProvider(movieRemoteClient: MovieRemoteClient): MovieRemoteDataSource =
        MovieRemoteDataSource(
            movieRemoteClient
        )
}