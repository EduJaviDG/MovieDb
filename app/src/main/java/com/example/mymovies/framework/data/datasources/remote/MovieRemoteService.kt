package com.example.mymovies.framework.data.datasources.remote

import com.example.mymovies.data.datasources.remote.MovieApiResult
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
    suspend fun getPopularMoviesWithAccessToken(@HeaderMap headers: Map<String, String>): MovieApiResult
}