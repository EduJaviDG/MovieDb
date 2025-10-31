package com.example.mymovies.data.datasources.remote

import com.example.mymovies.domain.model.MovieApiResult
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface MovieRemoteService {

    @GET("popular")
    suspend fun getPopularMoviesWithApiKey(
        @Query("api_key") apikey: String,
        @Query("language") language: String,
        @Query("region") region: String,
    ): MovieApiResult

    @GET("popular")
    suspend fun getPopularMoviesWithAccessToken(
        @Query("language") language: String,
        @Query("region") region: String,
        @HeaderMap headers: Map<String, String>,
        ): MovieApiResult
}