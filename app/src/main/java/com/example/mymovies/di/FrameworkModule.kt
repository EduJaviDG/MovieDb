package com.example.mymovies.di

import android.content.Context
import androidx.room.Room
import com.example.mymovies.data.datasources.local.AppDataBase
import com.example.mymovies.data.datasources.local.MovieDao
import com.example.mymovies.data.datasources.local.MovieLocalDataSource
import com.example.mymovies.data.datasources.remote.MovieRemoteClient
import com.example.mymovies.data.datasources.remote.MovieRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FrameworkModule {
    @Provides
    fun movieDbClientProvider(): MovieRemoteClient = MovieRemoteClient()

    @Provides
    fun movieRemoteDataSourceProvider(movieRemoteClient: MovieRemoteClient): MovieRemoteDataSource =
        MovieRemoteDataSource(
            movieRemoteClient
        )
    @Singleton
    @Provides
    fun provideAppDataBase(@ApplicationContext app: Context) =
        Room.databaseBuilder(
            app,
            AppDataBase::class.java,
            "database-name"
        )
            .build()
    @Singleton
    @Provides
    fun provideMovieDao(db: AppDataBase) =
        db.movieDao()
    @Provides
    fun movieLocalDataSourceProvider(dao: MovieDao): MovieLocalDataSource =
        MovieLocalDataSource(dao)

}