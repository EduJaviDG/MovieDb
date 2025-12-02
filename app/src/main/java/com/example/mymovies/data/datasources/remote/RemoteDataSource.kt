package com.example.mymovies.data.datasources.remote

import com.example.mymovies.domain.model.MoviesResponse
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getAllPopularMoviesWithApiKey(
        apikey: String?,
        language: String?,
        region: String?,
        page: Int?
    ): Response<MoviesResponse>

    suspend fun getAllPopularMoviesWithAccessToken(
        headers: Map<String, String>?,
        language: String?,
        region: String?,
        page: Int?
    ): Response<MoviesResponse>

}