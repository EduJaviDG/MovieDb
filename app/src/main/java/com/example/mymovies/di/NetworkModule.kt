package com.example.mymovies.di

import com.example.mymovies.data.datasources.remote.MovieRemoteDataSource
import com.example.mymovies.data.datasources.remote.MovieRemoteService
import com.example.mymovies.util.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    @Singleton
    @Provides
    fun service(retrofit: Retrofit): MovieRemoteService = retrofit.create(MovieRemoteService::class.java)

    @Singleton
    @Provides
    fun movieRemoteDataSourceProvider(movieRemoteService: MovieRemoteService): MovieRemoteDataSource =
        MovieRemoteDataSource(
            movieRemoteService
        )
}